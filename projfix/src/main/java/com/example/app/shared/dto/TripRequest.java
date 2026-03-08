package com.example.app.shared.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record TripRequest(
        @NotBlank String code,
        @NotBlank String origin,
        @NotBlank String destination,
        @NotNull @Future LocalDateTime departureTime,
        @NotNull @Min(0) Integer availableSeats,
        @NotBlank @Pattern(regexp = "SCHEDULED|CANCELLED|COMPLETED") String status
) {
}
