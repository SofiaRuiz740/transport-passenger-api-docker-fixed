package com.example.app.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.mysql")
public record MySqlProperties(String host, int port, String database, String username, String password) {
}
