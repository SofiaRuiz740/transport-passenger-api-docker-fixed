package com.example.app.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtAuthenticationWebFilter jwtAuthenticationWebFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/register", "/auth/login", "/health").permitAll()
                        .pathMatchers("/auth/register", "/auth/login").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/resourceA", "/api/resourceA/*").hasAnyRole("USER", "ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/resourceA").hasAnyRole("USER", "ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/resourceA/*").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/resourceA/*").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/resourceB", "/api/resourceB/*").hasAnyRole("USER", "ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/resourceB").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/resourceB/*").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/resourceB/*").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
