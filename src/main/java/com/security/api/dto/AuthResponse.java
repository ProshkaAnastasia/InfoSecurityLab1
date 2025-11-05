package com.security.api.dto;

public class AuthResponse {
    
    private String token;
    private String message;
    private String username;

    public AuthResponse(String token, String message, String username) {
        this.token = token;
        this.message = message;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }
}
