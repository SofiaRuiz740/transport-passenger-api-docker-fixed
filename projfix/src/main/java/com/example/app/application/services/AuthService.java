package com.example.app.application.services;

import com.example.app.domain.model.Role;
import com.example.app.domain.model.User;
import com.example.app.domain.ports.input.AuthUseCase;
import com.example.app.domain.ports.output.UserPersistencePort;
import com.example.app.infrastructure.security.JwtService;
import com.example.app.shared.dto.AuthResponse;
import com.example.app.shared.dto.LoginRequest;
import com.example.app.shared.dto.RegisterRequest;
import com.example.app.shared.exceptions.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class AuthService implements AuthUseCase {

    private final UserPersistencePort userPersistencePort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserPersistencePort userPersistencePort, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userPersistencePort = userPersistencePort;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public Mono<AuthResponse> register(RegisterRequest request) {
        return userPersistencePort.existsByEmail(request.email())
                .flatMap(existsEmail -> existsEmail
                        ? Mono.error(new ApiException(HttpStatus.CONFLICT, "Email already registered"))
                        : userPersistencePort.existsByUsername(request.username()))
                .flatMap(existsUsername -> existsUsername
                        ? Mono.error(new ApiException(HttpStatus.CONFLICT, "Username already registered"))
                        : userPersistencePort.save(new User(
                        null,
                        request.username(),
                        request.email(),
                        passwordEncoder.encode(request.password()),
                        Role.USER,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )))
                .map(this::buildAuthResponse);
    }

    @Override
    public Mono<AuthResponse> login(LoginRequest request) {
        return userPersistencePort.findByEmail(request.email())
                .switchIfEmpty(Mono.error(new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials")))
                .filter(user -> passwordEncoder.matches(request.password(), user.password()))
                .switchIfEmpty(Mono.error(new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials")))
                .map(this::buildAuthResponse);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(user.email(), user.role().name());
        return new AuthResponse(token, "Bearer", jwtService.getExpirationMinutes(), user.email(), user.role().name());
    }
}
