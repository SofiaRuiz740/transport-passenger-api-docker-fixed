package com.example.app.domain.ports.output;

import com.example.app.domain.model.Passenger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PassengerPersistencePort {
    Flux<Passenger> findAll();

    Mono<Passenger> findById(Long id);

    Mono<Passenger> save(Passenger passenger);

    Mono<Passenger> update(Passenger passenger);

    Mono<Void> deleteById(Long id);
}
