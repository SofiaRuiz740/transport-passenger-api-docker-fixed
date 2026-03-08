package com.example.app.infrastructure.adapters.input;

import com.example.app.domain.ports.input.PassengerUseCase;
import com.example.app.shared.dto.PassengerRequest;
import com.example.app.shared.dto.PassengerResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/resourceA")
public class PassengerController {

    private final PassengerUseCase passengerUseCase;

    public PassengerController(PassengerUseCase passengerUseCase) {
        this.passengerUseCase = passengerUseCase;
    }

    @GetMapping
    public Flux<PassengerResponse> findAll() {
        return passengerUseCase.findAll();
    }

    @GetMapping("/{id}")
    public Mono<PassengerResponse> findById(@PathVariable Long id) {
        return passengerUseCase.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PassengerResponse> create(@Valid @RequestBody Mono<PassengerRequest> request) {
        return request.flatMap(passengerUseCase::create);
    }

    @PutMapping("/{id}")
    public Mono<PassengerResponse> update(@PathVariable Long id, @Valid @RequestBody Mono<PassengerRequest> request) {
        return request.flatMap(body -> passengerUseCase.update(id, body));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return passengerUseCase.delete(id);
    }
}
