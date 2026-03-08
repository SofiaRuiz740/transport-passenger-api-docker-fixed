package com.example.app.domain.model;

import java.time.LocalDateTime;

public record User(
        Long id,
        String username,
        String email,
        String password,
        Role role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
