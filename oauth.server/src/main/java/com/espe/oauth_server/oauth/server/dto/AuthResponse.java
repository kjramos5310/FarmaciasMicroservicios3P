package com.espe.oauth_server.oauth.server.dto;

public class AuthResponse {
    
    private String message;
    private String username;
    private String email;
    private Long userId;
    
    // Constructores
    public AuthResponse() {
    }

    public AuthResponse(String message, String username, String email, Long userId) {
        this.message = message;
        this.username = username;
        this.email = email;
        this.userId = userId;
    }

    // Getters y Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
