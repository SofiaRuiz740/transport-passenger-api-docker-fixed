package com.example.app.shared.dto;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresInMinutes,
        String email,
        String role
) {
}
