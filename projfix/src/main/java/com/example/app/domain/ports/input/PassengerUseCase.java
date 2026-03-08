package com.example.app.domain.ports.input;

import com.example.app.shared.dto.PassengerRequest;
import com.example.app.shared.dto.PassengerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PassengerUseCase {
    Flux<PassengerResponse> findAll();

    Mono<PassengerResponse> findById(Long id);

    Mono<PassengerResponse> create(PassengerRequest request);

    Mono<PassengerResponse> update(Long id, PassengerRequest request);

    Mono<Void> delete(Long id);
}
