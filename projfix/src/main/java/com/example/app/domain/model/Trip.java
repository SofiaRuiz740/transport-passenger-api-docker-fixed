package com.example.app.domain.model;

import java.time.LocalDateTime;

public record Trip(
        Long id,
        String code,
        String origin,
        String destination,
        LocalDateTime departureTime,
        Integer availableSeats,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
