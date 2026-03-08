CREATE TABLE IF NOT EXISTS resource_b (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    origin VARCHAR(120) NOT NULL,
    destination VARCHAR(120) NOT NULL,
    departure_time DATETIME NOT NULL,
    available_seats INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);
