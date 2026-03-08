package com.example.app.shared.mapper;

import com.example.app.domain.model.Trip;
import com.example.app.shared.dto.TripRequest;
import com.example.app.shared.dto.TripResponse;

import java.time.LocalDateTime;

public final class TripMapper {

    private TripMapper() {
    }

    public static Trip toDomain(TripRequest request) {
        return new Trip(null, request.code(), request.origin(), request.destination(), request.departureTime(), request.availableSeats(), request.status(), null, null);
    }

    public static Trip toUpdatedDomain(Long id, TripRequest request, Trip current) {
        return new Trip(id, request.code(), request.origin(), request.destination(), request.departureTime(), request.availableSeats(), request.status(), current.createdAt(), LocalDateTime.now());
    }

    public static TripResponse toResponse(Trip trip) {
        return new TripResponse(
                trip.id(),
                trip.code(),
                trip.origin(),
                trip.destination(),
                trip.departureTime(),
                trip.availableSeats(),
                trip.status(),
                trip.createdAt(),
                trip.updatedAt()
        );
    }
}
