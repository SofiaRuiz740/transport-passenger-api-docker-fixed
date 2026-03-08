package com.example.app.domain.ports.input;

import com.example.app.shared.dto.AuthResponse;
import com.example.app.shared.dto.LoginRequest;
import com.example.app.shared.dto.RegisterRequest;
import reactor.core.publisher.Mono;

public interface AuthUseCase {
    Mono<AuthResponse> register(RegisterRequest request);

    Mono<AuthResponse> login(LoginRequest request);
}
