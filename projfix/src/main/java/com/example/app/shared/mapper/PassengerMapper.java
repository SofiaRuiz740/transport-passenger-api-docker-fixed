package com.example.app.shared.mapper;

import com.example.app.domain.model.Passenger;
import com.example.app.shared.dto.PassengerRequest;
import com.example.app.shared.dto.PassengerResponse;

import java.time.LocalDateTime;

public final class PassengerMapper {

    private PassengerMapper() {
    }

    public static Passenger toDomain(PassengerRequest request) {
        return new Passenger(null, request.fullName(), request.documentNumber(), request.email(), request.status(), null, null);
    }

    public static Passenger toUpdatedDomain(Long id, PassengerRequest request, Passenger current) {
        return new Passenger(id, request.fullName(), request.documentNumber(), request.email(), request.status(), current.createdAt(), LocalDateTime.now());
    }

    public static PassengerResponse toResponse(Passenger passenger) {
        return new PassengerResponse(
                passenger.id(),
                passenger.fullName(),
                passenger.documentNumber(),
                passenger.email(),
                passenger.status(),
                passenger.createdAt(),
                passenger.updatedAt()
        );
    }
}
