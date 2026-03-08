package com.example.app.domain.model;

import java.time.LocalDateTime;

public record Passenger(
        Long id,
        String fullName,
        String documentNumber,
        String email,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
