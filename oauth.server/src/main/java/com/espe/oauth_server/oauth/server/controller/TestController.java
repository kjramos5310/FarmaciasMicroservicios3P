package com.espe.oauth_server.oauth.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @Autowired(required = false)
    private OAuth2AuthorizationService authorizationService;

    @Autowired(required = false)
    private JwtDecoder jwtDecoder;

    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> response = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        response.put("message", "OAuth Server is running");
        response.put("authenticated", auth != null && auth.isAuthenticated());
        response.put("principal", auth != null ? auth.getName() : "anonymous");

        return response;
    }

    @GetMapping("/user-test")
    public Map<String, String> userTest() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "User endpoint test");
        response.put("username", "juan");
        response.put("password", "12345 (with {noop})");
        return response;
    }

    @GetMapping("/protected")
    public Map<String, Object> protectedEndpoint() {
        Map<String, Object> response = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        response.put("message", "Acceso a recurso protegido exitoso");
        response.put("authenticatedUser", auth.getName());
        response.put("authorities", auth.getAuthorities());
        response.put("timestamp", Instant.now());
        
        return response;
    }

    @GetMapping("/token-info")
    public Map<String, Object> tokenInfo(@RequestParam(required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        
        if (token == null || token.trim().isEmpty()) {
            response.put("error", "Token no proporcionado");
            response.put("usage", "Usa: /token-info?token=TU_JWT_TOKEN");
            response.put("hint", "Obtén el token desde http://localhost:8002/authorized después de autenticarte");
            return response;
        }

        // Limpiar el token (remover &continue u otros parámetros)
        token = token.split("&")[0].trim();
        
        try {
            // Decodificar el JWT
            if (jwtDecoder != null) {
                Jwt jwt = jwtDecoder.decode(token);
                
                response.put("status", "Token válido");
                response.put("type", "JWT");
                response.put("subject", jwt.getSubject());
                response.put("issuer", jwt.getIssuer());
                response.put("audience", jwt.getAudience());
                response.put("issuedAt", jwt.getIssuedAt());
                response.put("expiresAt", jwt.getExpiresAt());
                response.put("notBefore", jwt.getNotBefore());
                response.put("claims", jwt.getClaims());
                
                // Verificar si está expirado
                if (jwt.getExpiresAt() != null && jwt.getExpiresAt().isBefore(Instant.now())) {
                    response.put("warning", "⚠️ Token expirado");
                    response.put("expired", true);
                } else {
                    response.put("expired", false);
                }
                
            } else {
                response.put("error", "JwtDecoder no disponible");
            }
            
        } catch (JwtException e) {
            response.put("error", "Token inválido o corrupto");
            response.put("details", e.getMessage());
            response.put("tokenPreview", token.substring(0, Math.min(50, token.length())) + "...");
        } catch (Exception e) {
            response.put("error", "Error al procesar el token");
            response.put("details", e.getMessage());
        }
        
        return response;
    }
}
