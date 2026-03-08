package com.example.app.infrastructure.adapters.input;

import com.example.app.domain.ports.input.TripUseCase;
import com.example.app.shared.dto.TripRequest;
import com.example.app.shared.dto.TripResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/resourceB")
public class TripController {

    private final TripUseCase tripUseCase;

    public TripController(TripUseCase tripUseCase) {
        this.tripUseCase = tripUseCase;
    }

    @GetMapping
    public Flux<TripResponse> findAll() {
        return tripUseCase.findAll();
    }

    @GetMapping("/{id}")
    public Mono<TripResponse> findById(@PathVariable Long id) {
        return tripUseCase.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TripResponse> create(@Valid @RequestBody Mono<TripRequest> request) {
        return request.flatMap(tripUseCase::create);
    }

    @PutMapping("/{id}")
    public Mono<TripResponse> update(@PathVariable Long id, @Valid @RequestBody Mono<TripRequest> request) {
        return request.flatMap(body -> tripUseCase.update(id, body));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return tripUseCase.delete(id);
    }
}
