package com.example.app;

import com.example.app.infrastructure.config.JwtProperties;
import com.example.app.infrastructure.config.MySqlProperties;
import com.example.app.infrastructure.config.PostgresProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, PostgresProperties.class, MySqlProperties.class})
public class TransportPassengerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransportPassengerApiApplication.class, args);
    }
}
