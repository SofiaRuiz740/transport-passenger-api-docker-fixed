package com.example.app.infrastructure.config;

import com.example.app.domain.model.Role;
import com.example.app.domain.model.User;
import com.example.app.domain.ports.output.UserPersistencePort;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class BootstrapDataConfig {

    @Bean
    public ApplicationRunner defaultAdminInitializer(UserPersistencePort userPersistencePort, PasswordEncoder passwordEncoder) {
        return args -> userPersistencePort.existsByEmail("admin@transport.local")
                .flatMap(exists -> exists ? reactor.core.publisher.Mono.empty() : userPersistencePort.save(new User(
                        null,
                        "admin",
                        "admin@transport.local",
                        passwordEncoder.encode("Admin123!"),
                        Role.ADMIN,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )).then())
                .subscribe();
    }
}
