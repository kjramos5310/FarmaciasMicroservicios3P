package com.espe.oauth_server.oauth.server.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class UserInfoController {

    @GetMapping("/userinfo")
    public Map<String, Object> userInfo(Authentication authentication) {
        Map<String, Object> userInfo = new HashMap<>();
        
        if (authentication != null) {
            userInfo.put("sub", authentication.getName());
            userInfo.put("name", authentication.getName());
            userInfo.put("preferred_username", authentication.getName());
            userInfo.put("email", authentication.getName() + "@example.com");
            userInfo.put("email_verified", true);
            
            // Agregar roles/authorities
            userInfo.put("roles", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
            
            // Si es un JWT token, incluir claims adicionales
            if (authentication instanceof JwtAuthenticationToken) {
                JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
                userInfo.put("token_type", "JWT");
            }
        }
        
        return userInfo;
    }
}
