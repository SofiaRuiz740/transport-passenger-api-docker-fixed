package com.example.app.infrastructure.adapters.input;

import com.example.app.domain.ports.input.AuthUseCase;
import com.example.app.shared.dto.AuthResponse;
import com.example.app.shared.dto.LoginRequest;
import com.example.app.shared.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AuthResponse> register(@Valid @RequestBody Mono<RegisterRequest> request) {
        return request.flatMap(authUseCase::register);
    }

    @PostMapping("/login")
    public Mono<AuthResponse> login(@Valid @RequestBody Mono<LoginRequest> request) {
        return request.flatMap(authUseCase::login);
    }
}
