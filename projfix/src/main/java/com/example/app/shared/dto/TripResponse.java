package com.example.app.shared.dto;

import java.time.LocalDateTime;

public record TripResponse(
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
