package com.example.app.shared.exceptions;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@Component
@Order(-2)
public class GlobalExceptionHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = ex.getMessage();

        if (ex instanceof ApiException apiException) {
            status = apiException.getStatus();
            message = apiException.getMessage();
        } else if (ex instanceof WebExchangeBindException bindException) {
            status = HttpStatus.BAD_REQUEST;
            message = bindException.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
        }

        ErrorResponse response = new ErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                exchange.getRequest().getPath().value()
        );

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = ("{" +
                "\"timestamp\":\"" + response.timestamp() + "\"," +
                "\"status\":" + response.status() + "," +
                "\"error\":\"" + response.error() + "\"," +
                "\"message\":\"" + escape(response.message()) + "\"," +
                "\"path\":\"" + response.path() + "\"}").getBytes();
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("\"", "'");
    }
}
