package com.example.app.domain.ports.output;

import com.example.app.domain.model.User;
import reactor.core.publisher.Mono;

public interface UserPersistencePort {
    Mono<User> save(User user);

    Mono<User> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByUsername(String username);
}
