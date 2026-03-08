package com.example.app.infrastructure.adapters.output.postgres;

import com.example.app.domain.model.Passenger;
import com.example.app.domain.ports.output.PassengerPersistencePort;
import io.r2dbc.spi.Row;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class PostgresPassengerAdapter implements PassengerPersistencePort {

    private final DatabaseClient databaseClient;

    public PostgresPassengerAdapter(@Qualifier("postgresTemplate") R2dbcEntityTemplate template) {
        this.databaseClient = template.getDatabaseClient();
    }

    @Override
    public Flux<Passenger> findAll() {
        return databaseClient.sql("SELECT id, full_name, document_number, email, status, created_at, updated_at FROM resource_a ORDER BY id")
                .map((row, metadata) -> mapPassenger(row))
                .all();
    }

    @Override
    public Mono<Passenger> findById(Long id) {
        return databaseClient.sql("SELECT id, full_name, document_number, email, status, created_at, updated_at FROM resource_a WHERE id = :id")
                .bind("id", id)
                .map((row, metadata) -> mapPassenger(row))
                .one();
    }

    @Override
    public Mono<Passenger> save(Passenger passenger) {
        return databaseClient.sql("""
                        INSERT INTO resource_a (full_name, document_number, email, status, created_at, updated_at)
                        VALUES (:fullName, :documentNumber, :email, :status, :createdAt, :updatedAt)
                        RETURNING id, full_name, document_number, email, status, created_at, updated_at
                        """)
                .bind("fullName", passenger.fullName())
                .bind("documentNumber", passenger.documentNumber())
                .bind("email", passenger.email())
                .bind("status", passenger.status())
                .bind("createdAt", valueOrNow(passenger.createdAt()))
                .bind("updatedAt", valueOrNow(passenger.updatedAt()))
                .map((row, metadata) -> mapPassenger(row))
                .one();
    }

    @Override
    public Mono<Passenger> update(Passenger passenger) {
        return databaseClient.sql("""
                        UPDATE resource_a
                        SET full_name = :fullName,
                            document_number = :documentNumber,
                            email = :email,
                            status = :status,
                            updated_at = :updatedAt
                        WHERE id = :id
                        RETURNING id, full_name, document_number, email, status, created_at, updated_at
                        """)
                .bind("id", passenger.id())
                .bind("fullName", passenger.fullName())
                .bind("documentNumber", passenger.documentNumber())
                .bind("email", passenger.email())
                .bind("status", passenger.status())
                .bind("updatedAt", valueOrNow(passenger.updatedAt()))
                .map((row, metadata) -> mapPassenger(row))
                .one();
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return databaseClient.sql("DELETE FROM resource_a WHERE id = :id")
                .bind("id", id)
                .fetch()
                .rowsUpdated()
                .then();
    }

    private Passenger mapPassenger(Row row) {
        return new Passenger(
                row.get("id", Long.class),
                row.get("full_name", String.class),
                row.get("document_number", String.class),
                row.get("email", String.class),
                row.get("status", String.class),
                row.get("created_at", LocalDateTime.class),
                row.get("updated_at", LocalDateTime.class)
        );
    }

    private LocalDateTime valueOrNow(LocalDateTime value) {
        return value == null ? LocalDateTime.now() : value;
    }
}
