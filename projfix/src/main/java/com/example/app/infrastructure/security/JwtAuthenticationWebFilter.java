package com.example.app.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationWebFilter implements WebFilter {

    private final JwtService jwtService;

    public JwtAuthenticationWebFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = header.substring(7);
        try {
            Claims claims = jwtService.parseToken(token);
            String role = claims.get("role", String.class);
            String subject = claims.getSubject();
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    subject,
                    token,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );
            return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        } catch (JwtException | IllegalArgumentException ex) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
