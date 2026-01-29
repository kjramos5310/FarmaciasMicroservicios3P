package com.espe.oauth_server.oauth.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth2")
public class TokenController {

    @Autowired(required = false)
    private OAuth2AuthorizationService authorizationService;

    /**
     * Token Revocation - Revoca un access token o refresh token
     * POST /oauth2/revoke
     */
    @PostMapping("/revoke")
    public ResponseEntity<Map<String, Object>> revokeToken(
            @RequestParam("token") String token,
            @RequestParam(value = "token_type_hint", required = false) String tokenTypeHint) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (authorizationService == null) {
            response.put("error", "Authorization service not available");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
        
        try {
            // Intentar encontrar el token (puede ser access token o refresh token)
            OAuth2TokenType tokenType = null;
            if ("refresh_token".equals(tokenTypeHint)) {
                tokenType = OAuth2TokenType.REFRESH_TOKEN;
            } else {
                tokenType = OAuth2TokenType.ACCESS_TOKEN;
            }
            
            OAuth2Authorization authorization = authorizationService.findByToken(token, tokenType);
            
            if (authorization != null) {
                // Remover la autorización (revocar todos los tokens asociados)
                authorizationService.remove(authorization);
                
                response.put("status", "revoked");
                response.put("message", "Token revocado exitosamente");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "not_found");
                response.put("message", "Token no encontrado o ya revocado");
                return ResponseEntity.ok(response);
            }
            
        } catch (Exception e) {
            response.put("error", "Error al revocar token");
            response.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Token Introspection - Verifica si un token es válido
     * POST /oauth2/introspect
     */
    @PostMapping("/introspect")
    public ResponseEntity<Map<String, Object>> introspectToken(@RequestParam("token") String token) {
        Map<String, Object> response = new HashMap<>();
        
        if (authorizationService == null) {
            response.put("active", false);
            return ResponseEntity.ok(response);
        }
        
        try {
            OAuth2Authorization authorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
            
            if (authorization != null && authorization.getAccessToken() != null) {
                response.put("active", true);
                response.put("scope", authorization.getAuthorizedScopes());
                response.put("client_id", authorization.getRegisteredClientId());
                response.put("username", authorization.getPrincipalName());
                response.put("token_type", "Bearer");
                
                if (authorization.getAccessToken().getToken().getExpiresAt() != null) {
                    response.put("exp", authorization.getAccessToken().getToken().getExpiresAt().getEpochSecond());
                }
                if (authorization.getAccessToken().getToken().getIssuedAt() != null) {
                    response.put("iat", authorization.getAccessToken().getToken().getIssuedAt().getEpochSecond());
                }
            } else {
                response.put("active", false);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("active", false);
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
