package com.example.app.application.services;

import com.example.app.domain.ports.input.PassengerUseCase;
import com.example.app.domain.ports.output.PassengerPersistencePort;
import com.example.app.shared.dto.PassengerRequest;
import com.example.app.shared.dto.PassengerResponse;
import com.example.app.shared.exceptions.ApiException;
import com.example.app.shared.mapper.PassengerMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PassengerService implements PassengerUseCase {

    private final PassengerPersistencePort passengerPersistencePort;

    public PassengerService(PassengerPersistencePort passengerPersistencePort) {
        this.passengerPersistencePort = passengerPersistencePort;
    }

    @Override
    public Flux<PassengerResponse> findAll() {
        return passengerPersistencePort.findAll().map(PassengerMapper::toResponse);
    }

    @Override
    public Mono<PassengerResponse> findById(Long id) {
        return passengerPersistencePort.findById(id)
                .switchIfEmpty(Mono.error(new ApiException(HttpStatus.NOT_FOUND, "Passenger not found")))
                .map(PassengerMapper::toResponse);
    }

    @Override
    public Mono<PassengerResponse> create(PassengerRequest request) {
        return passengerPersistencePort.save(PassengerMapper.toDomain(request)).map(PassengerMapper::toResponse);
    }

    @Override
    public Mono<PassengerResponse> update(Long id, PassengerRequest request) {
        return passengerPersistencePort.findById(id)
                .switchIfEmpty(Mono.error(new ApiException(HttpStatus.NOT_FOUND, "Passenger not found")))
                .map(current -> PassengerMapper.toUpdatedDomain(id, request, current))
                .flatMap(passengerPersistencePort::update)
                .map(PassengerMapper::toResponse);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return passengerPersistencePort.findById(id)
                .switchIfEmpty(Mono.error(new ApiException(HttpStatus.NOT_FOUND, "Passenger not found")))
                .flatMap(passenger -> passengerPersistencePort.deleteById(id));
    }
}
