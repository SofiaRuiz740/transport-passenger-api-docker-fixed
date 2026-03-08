package com.example.app;

import com.example.app.application.services.AuthService;
import com.example.app.domain.model.Passenger;
import com.example.app.domain.model.Role;
import com.example.app.domain.model.Trip;
import com.example.app.domain.model.User;
import com.example.app.domain.ports.input.PassengerUseCase;
import com.example.app.domain.ports.input.TripUseCase;
import com.example.app.domain.ports.output.UserPersistencePort;
import com.example.app.infrastructure.adapters.input.AuthController;
import com.example.app.infrastructure.adapters.input.PassengerController;
import com.example.app.infrastructure.adapters.input.TripController;
import com.example.app.infrastructure.config.JwtProperties;
import com.example.app.infrastructure.security.JwtAuthenticationWebFilter;
import com.example.app.infrastructure.security.JwtService;
import com.example.app.infrastructure.security.SecurityConfig;
import com.example.app.shared.dto.PassengerRequest;
import com.example.app.shared.dto.PassengerResponse;
import com.example.app.shared.dto.TripRequest;
import com.example.app.shared.dto.TripResponse;
import com.example.app.shared.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {AuthController.class, PassengerController.class, TripController.class})
@Import({SecurityConfig.class, JwtAuthenticationWebFilter.class, JwtService.class, AuthService.class, GlobalExceptionHandler.class, TransportPassengerApiApplicationTests.TestProps.class})
class TransportPassengerApiApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserPersistencePort userPersistencePort;

    @MockBean
    private PassengerUseCase passengerUseCase;

    @MockBean
    private TripUseCase tripUseCase;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        adminToken = jwtService.generateToken("admin@transport.local", "ADMIN");
        userToken = jwtService.generateToken("user@transport.local", "USER");
    }

    @Test
    void shouldRegisterUserAndReturnJwt() {
        when(userPersistencePort.existsByEmail("new@user.com")).thenReturn(Mono.just(false));
        when(userPersistencePort.existsByUsername("newuser")).thenReturn(Mono.just(false));
        when(userPersistencePort.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return Mono.just(new User(1L, user.username(), user.email(), user.password(), Role.USER, LocalDateTime.now(), LocalDateTime.now()));
        });

        webTestClient.post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "username":"newuser",
                          "email":"new@user.com",
                          "password":"Password123"
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.token").isNotEmpty()
                .jsonPath("$.role").isEqualTo("USER");
    }

    @Test
    void shouldLoginUserAndReturnJwt() {
        when(userPersistencePort.findByEmail("user@transport.local")).thenReturn(Mono.just(
                new User(2L, "user", "user@transport.local", passwordEncoder.encode("Password123"), Role.USER, LocalDateTime.now(), LocalDateTime.now())
        ));

        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "email":"user@transport.local",
                          "password":"Password123"
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token").isNotEmpty()
                .jsonPath("$.role").isEqualTo("USER");
    }

    @Test
    void shouldAllowUserReadAndCreatePassengerButForbidUpdate() {
        PassengerResponse passenger = new PassengerResponse(1L, "Ana Perez", "CC123", "ana@correo.com", "ACTIVE", LocalDateTime.now(), LocalDateTime.now());
        PassengerRequest request = new PassengerRequest("Ana Perez", "CC123", "ana@correo.com", "ACTIVE");
        when(passengerUseCase.findAll()).thenReturn(Flux.just(passenger));
        when(passengerUseCase.create(any(PassengerRequest.class))).thenReturn(Mono.just(passenger));

        webTestClient.get()
                .uri("/api/resourceA")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1);

        webTestClient.post()
                .uri("/api/resourceA")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.put()
                .uri("/api/resourceA/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void shouldAllowAdminFullCrudOnPassenger() {
        PassengerResponse passenger = new PassengerResponse(1L, "Ana Perez", "CC123", "ana@correo.com", "ACTIVE", LocalDateTime.now(), LocalDateTime.now());
        PassengerRequest request = new PassengerRequest("Ana Perez", "CC123", "ana@correo.com", "ACTIVE");
        when(passengerUseCase.update(eq(1L), any(PassengerRequest.class))).thenReturn(Mono.just(passenger));
        when(passengerUseCase.delete(anyLong())).thenReturn(Mono.empty());

        webTestClient.put()
                .uri("/api/resourceA/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();

        webTestClient.delete()
                .uri("/api/resourceA/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldRestrictTripWriteOperationsToAdmin() {
        TripResponse trip = new TripResponse(1L, "TR-001", "Bogota", "Medellin", LocalDateTime.now().plusDays(1), 30, "SCHEDULED", LocalDateTime.now(), LocalDateTime.now());
        TripRequest request = new TripRequest("TR-001", "Bogota", "Medellin", LocalDateTime.now().plusDays(1), 30, "SCHEDULED");
        when(tripUseCase.findAll()).thenReturn(Flux.just(trip));
        when(tripUseCase.create(any(TripRequest.class))).thenReturn(Mono.just(trip));

        webTestClient.get()
                .uri("/api/resourceB")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                .exchange()
                .expectStatus().isOk();

        webTestClient.post()
                .uri("/api/resourceB")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isForbidden();

        webTestClient.post()
                .uri("/api/resourceB")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated();
    }

    @TestConfiguration
    static class TestProps {
        @Bean
        JwtProperties jwtProperties() {
            return new JwtProperties("ChangeThisSecretKeyForJwtSigning1234567890", 60);
        }
    }
}
