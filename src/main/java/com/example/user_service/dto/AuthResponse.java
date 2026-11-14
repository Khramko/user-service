package com.example.user_service.dto;

public class AuthResponse {
    private Long id;
    private String token;

    public AuthResponse() {}

    public AuthResponse(Long id, String token) {
        this.id = id;
        this.token = token;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}