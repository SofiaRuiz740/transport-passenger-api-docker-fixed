package com.example.app.infrastructure.adapters.output.mysql;

import com.example.app.domain.model.Trip;
import com.example.app.domain.ports.output.TripPersistencePort;
import io.r2dbc.spi.Row;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class MySqlTripAdapter implements TripPersistencePort {

    private final DatabaseClient databaseClient;

    public MySqlTripAdapter(@Qualifier("mysqlTemplate") R2dbcEntityTemplate template) {
        this.databaseClient = template.getDatabaseClient();
    }

    @Override
    public Flux<Trip> findAll() {
        return databaseClient.sql("SELECT id, code, origin, destination, departure_time, available_seats, status, created_at, updated_at FROM resource_b ORDER BY id")
                .map((row, metadata) -> mapTrip(row))
                .all();
    }

    @Override
    public Mono<Trip> findById(Long id) {
        return databaseClient.sql("SELECT id, code, origin, destination, departure_time, available_seats, status, created_at, updated_at FROM resource_b WHERE id = :id")
                .bind("id", id)
                .map((row, metadata) -> mapTrip(row))
                .one();
    }

    @Override
    public Mono<Trip> save(Trip trip) {
        return databaseClient.sql("""
                        INSERT INTO resource_b (code, origin, destination, departure_time, available_seats, status, created_at, updated_at)
                        VALUES (:code, :origin, :destination, :departureTime, :availableSeats, :status, :createdAt, :updatedAt)
                        """)
                .bind("code", trip.code())
                .bind("origin", trip.origin())
                .bind("destination", trip.destination())
                .bind("departureTime", trip.departureTime())
                .bind("availableSeats", trip.availableSeats())
                .bind("status", trip.status())
                .bind("createdAt", valueOrNow(trip.createdAt()))
                .bind("updatedAt", valueOrNow(trip.updatedAt()))
                .fetch()
                .rowsUpdated()
                .then(findByCode(trip.code()));
    }

    @Override
    public Mono<Trip> update(Trip trip) {
        return databaseClient.sql("""
                        UPDATE resource_b
                        SET code = :code,
                            origin = :origin,
                            destination = :destination,
                            departure_time = :departureTime,
                            available_seats = :availableSeats,
                            status = :status,
                            updated_at = :updatedAt
                        WHERE id = :id
                        """)
                .bind("id", trip.id())
                .bind("code", trip.code())
                .bind("origin", trip.origin())
                .bind("destination", trip.destination())
                .bind("departureTime", trip.departureTime())
                .bind("availableSeats", trip.availableSeats())
                .bind("status", trip.status())
                .bind("updatedAt", valueOrNow(trip.updatedAt()))
                .fetch()
                .rowsUpdated()
                .then(findById(trip.id()));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return databaseClient.sql("DELETE FROM resource_b WHERE id = :id")
                .bind("id", id)
                .fetch()
                .rowsUpdated()
                .then();
    }

    private Mono<Trip> findByCode(String code) {
        return databaseClient.sql("SELECT id, code, origin, destination, departure_time, available_seats, status, created_at, updated_at FROM resource_b WHERE code = :code ORDER BY id DESC LIMIT 1")
                .bind("code", code)
                .map((row, metadata) -> mapTrip(row))
                .one();
    }

    private Trip mapTrip(Row row) {
        return new Trip(
                row.get("id", Long.class),
                row.get("code", String.class),
                row.get("origin", String.class),
                row.get("destination", String.class),
                row.get("departure_time", LocalDateTime.class),
                row.get("available_seats", Integer.class),
                row.get("status", String.class),
                row.get("created_at", LocalDateTime.class),
                row.get("updated_at", LocalDateTime.class)
        );
    }

    private LocalDateTime valueOrNow(LocalDateTime value) {
        return value == null ? LocalDateTime.now() : value;
    }
}
