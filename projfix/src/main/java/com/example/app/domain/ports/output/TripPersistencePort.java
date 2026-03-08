package com.example.app.domain.ports.output;

import com.example.app.domain.model.Trip;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TripPersistencePort {
    Flux<Trip> findAll();

    Mono<Trip> findById(Long id);

    Mono<Trip> save(Trip trip);

    Mono<Trip> update(Trip trip);

    Mono<Void> deleteById(Long id);
}
