package com.example.app.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.postgres")
public record PostgresProperties(String host, int port, String database, String username, String password) {
}
