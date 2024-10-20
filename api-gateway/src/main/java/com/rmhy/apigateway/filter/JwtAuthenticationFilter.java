package com.rmhy.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    @Value("${jwt.secretKey}")
    String secretKey;

    private static final List<String> PUBLIC_ROUTES = Arrays.asList(
            "/user-service/api/auth/.*",
            "/user-service/api/v2/auth/.*",
            "/user-service/api/v3/auth/.*",
            "/user-service/actuator/health",
            "/post-service/actuator/health",
            "/actuator/health"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Skip JWT filter for public routes
        if (isPublicRoute(path)) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        if (token != null && validateToken(token)) {
            return chain.filter(exchange);  // Continue if token is valid
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);  // Unauthorized if token invalid
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isPublicRoute(String path) {
        return PUBLIC_ROUTES.stream()
                .anyMatch(path::matches);
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            // Check if token has expired
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(signKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey signKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
