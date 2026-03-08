package com.example.app.application.services;

import com.example.app.domain.ports.input.TripUseCase;
import com.example.app.domain.ports.output.TripPersistencePort;
import com.example.app.shared.dto.TripRequest;
import com.example.app.shared.dto.TripResponse;
import com.example.app.shared.exceptions.ApiException;
import com.example.app.shared.mapper.TripMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TripService implements TripUseCase {

    private final TripPersistencePort tripPersistencePort;

    public TripService(TripPersistencePort tripPersistencePort) {
        this.tripPersistencePort = tripPersistencePort;
    }

    @Override
    public Flux<TripResponse> findAll() {
        return tripPersistencePort.findAll().map(TripMapper::toResponse);
    }

    @Override
    public Mono<TripResponse> findById(Long id) {
        return tripPersistencePort.findById(id)
                .switchIfEmpty(Mono.error(new ApiException(HttpStatus.NOT_FOUND, "Trip not found")))
                .map(TripMapper::toResponse);
    }

    @Override
    public Mono<TripResponse> create(TripRequest request) {
        return tripPersistencePort.save(TripMapper.toDomain(request)).map(TripMapper::toResponse);
    }

    @Override
    public Mono<TripResponse> update(Long id, TripRequest request) {
        return tripPersistencePort.findById(id)
                .switchIfEmpty(Mono.error(new ApiException(HttpStatus.NOT_FOUND, "Trip not found")))
                .map(current -> TripMapper.toUpdatedDomain(id, request, current))
                .flatMap(tripPersistencePort::update)
                .map(TripMapper::toResponse);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return tripPersistencePort.findById(id)
                .switchIfEmpty(Mono.error(new ApiException(HttpStatus.NOT_FOUND, "Trip not found")))
                .flatMap(trip -> tripPersistencePort.deleteById(id));
    }
}
