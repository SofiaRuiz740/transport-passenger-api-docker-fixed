package com.example.app.infrastructure.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ValidationDepth;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import java.time.Duration;

@Configuration
public class DatabaseConfig {

    @Bean("postgresConnectionFactory")
    public ConnectionFactory postgresConnectionFactory(PostgresProperties properties) {
        PostgresqlConnectionFactory factory = new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .host(properties.host())
                        .port(properties.port())
                        .database(properties.database())
                        .username(properties.username())
                        .password(properties.password())
                        .build()
        );
        return new ConnectionPool(ConnectionPoolConfiguration.builder(factory)
                .maxIdleTime(Duration.ofMinutes(30))
                .maxSize(20)
                .validationDepth(ValidationDepth.LOCAL)
                .build());
    }

    @Bean("mysqlConnectionFactory")
    public ConnectionFactory mysqlConnectionFactory(MySqlProperties properties) {
        io.asyncer.r2dbc.mysql.MySqlConnectionFactory factory =
                io.asyncer.r2dbc.mysql.MySqlConnectionFactory.from(
                        io.asyncer.r2dbc.mysql.MySqlConnectionConfiguration.builder()
                                .host(properties.host())
                                .port(properties.port())
                                .database(properties.database())
                                .user(properties.username())
                                .password(properties.password())
                                .build());
        return new ConnectionPool(ConnectionPoolConfiguration.builder(factory)
                .maxIdleTime(Duration.ofMinutes(30))
                .maxSize(20)
                .validationDepth(ValidationDepth.LOCAL)
                .build());
    }

    @Bean("postgresTemplate")
    public R2dbcEntityTemplate postgresTemplate(@Qualifier("postgresConnectionFactory") ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }

    @Bean("mysqlTemplate")
    public R2dbcEntityTemplate mysqlTemplate(@Qualifier("mysqlConnectionFactory") ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }

    @Bean
    public ConnectionFactoryInitializer postgresInitializer(@Qualifier("postgresConnectionFactory") ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema-postgres.sql")));
        return initializer;
    }

    @Bean
    public ConnectionFactoryInitializer mysqlInitializer(@Qualifier("mysqlConnectionFactory") ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema-mysql.sql")));
        return initializer;
    }
}
