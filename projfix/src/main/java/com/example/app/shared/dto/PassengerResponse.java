package com.example.app.shared.dto;

import java.time.LocalDateTime;

public record PassengerResponse(
        Long id,
        String fullName,
        String documentNumber,
        String email,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
