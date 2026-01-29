package com.example.gateway.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public AuthController(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @GetMapping("/")
    public Map<String, Object> home(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Gateway OAuth2 - Authenticated");
        response.put("user", authentication.getName());
        response.put("authorities", authentication.getAuthorities());
        return response;
    }

    @GetMapping("/token")
    public Map<String, Object> getToken(OAuth2AuthenticationToken authentication) {
        Map<String, Object> response = new HashMap<>();
        
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
            authentication.getAuthorizedClientRegistrationId(),
            authentication.getName()
        );
        
        if (authorizedClient != null) {
            response.put("accessToken", authorizedClient.getAccessToken().getTokenValue());
            response.put("refreshToken", 
                authorizedClient.getRefreshToken() != null ? 
                authorizedClient.getRefreshToken().getTokenValue() : null
            );
            response.put("expiresAt", authorizedClient.getAccessToken().getExpiresAt());
        }
        
        return response;
    }
}
