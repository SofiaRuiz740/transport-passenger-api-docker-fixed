package com.example.app.shared.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PassengerRequest(
        @NotBlank String fullName,
        @NotBlank String documentNumber,
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "ACTIVE|INACTIVE") String status
) {
}
