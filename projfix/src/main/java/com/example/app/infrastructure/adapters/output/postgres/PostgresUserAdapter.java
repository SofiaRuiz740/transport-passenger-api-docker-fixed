package com.example.app.infrastructure.adapters.output.postgres;

import com.example.app.domain.model.Role;
import com.example.app.domain.model.User;
import com.example.app.domain.ports.output.UserPersistencePort;
import io.r2dbc.spi.Row;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class PostgresUserAdapter implements UserPersistencePort {

    private final DatabaseClient databaseClient;

    public PostgresUserAdapter(@Qualifier("postgresTemplate") R2dbcEntityTemplate template) {
        this.databaseClient = template.getDatabaseClient();
    }

    @Override
    public Mono<User> save(User user) {
        return databaseClient.sql("""
                        INSERT INTO users (username, email, password, role, created_at, updated_at)
                        VALUES (:username, :email, :password, :role, :createdAt, :updatedAt)
                        RETURNING id, username, email, password, role, created_at, updated_at
                        """)
                .bind("username", user.username())
                .bind("email", user.email())
                .bind("password", user.password())
                .bind("role", user.role().name())
                .bind("createdAt", valueOrNow(user.createdAt()))
                .bind("updatedAt", valueOrNow(user.updatedAt()))
                .map((row, metadata) -> mapUser(row))
                .one();
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return databaseClient.sql("SELECT id, username, email, password, role, created_at, updated_at FROM users WHERE email = :email")
                .bind("email", email)
                .map((row, metadata) -> mapUser(row))
                .one();
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return databaseClient.sql("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email) AS exists")
                .bind("email", email)
                .map((row, metadata) -> Boolean.TRUE.equals(row.get("exists", Boolean.class)))
                .one();
    }

    @Override
    public Mono<Boolean> existsByUsername(String username) {
        return databaseClient.sql("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username) AS exists")
                .bind("username", username)
                .map((row, metadata) -> Boolean.TRUE.equals(row.get("exists", Boolean.class)))
                .one();
    }

    private User mapUser(Row row) {
        return new User(
                row.get("id", Long.class),
                row.get("username", String.class),
                row.get("email", String.class),
                row.get("password", String.class),
                Role.valueOf(row.get("role", String.class)),
                row.get("created_at", LocalDateTime.class),
                row.get("updated_at", LocalDateTime.class)
        );
    }

    private LocalDateTime valueOrNow(LocalDateTime value) {
        return value == null ? LocalDateTime.now() : value;
    }
}
