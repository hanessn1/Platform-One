package com.platformone.gateway.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        if (path.startsWith("/auth/") || path.startsWith("/actuator")) {
            return chain.filter(exchange);
        }

        List<String> authHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION);

        if (authHeaders == null || authHeaders.isEmpty() || !authHeaders.getFirst().startsWith("Bearer ")) {
            return this.onError(exchange, "Authorization token is missing or malformed.", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeaders.getFirst().substring(7);
        boolean tokenIsValid = jwtUtils.validateToken(token);
        if (!tokenIsValid) {
            return this.onError(exchange, "Invalid or expired JWT token.", HttpStatus.UNAUTHORIZED);
        }

        String userEmail = jwtUtils.extractEmail(token);
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-Auth-User-Email", userEmail)
                .header(HttpHeaders.AUTHORIZATION, authHeaders.getFirst())
                .build();
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorJson = String.format("{\"status\":%d, \"error\":\"%s\", \"message\":\"%s\", \"path\":\"%s\"}",
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                message,
                exchange.getRequest().getPath());

        return response.writeWith(Mono.just(response.bufferFactory().wrap(errorJson.getBytes())));
    }

    @Override
    public int getOrder() {
        return -200;
    }
}