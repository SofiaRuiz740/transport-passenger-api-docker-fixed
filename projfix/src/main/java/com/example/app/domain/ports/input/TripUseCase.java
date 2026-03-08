package com.example.app.domain.ports.input;

import com.example.app.shared.dto.TripRequest;
import com.example.app.shared.dto.TripResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TripUseCase {
    Flux<TripResponse> findAll();

    Mono<TripResponse> findById(Long id);

    Mono<TripResponse> create(TripRequest request);

    Mono<TripResponse> update(Long id, TripRequest request);

    Mono<Void> delete(Long id);
}
