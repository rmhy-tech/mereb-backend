package com.rmhy.apigateway.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

import static org.mockito.Mockito.*;

public class JwtAuthenticationFilterTest {
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private WebFilterChain chain;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthenticationFilter = new JwtAuthenticationFilter();
        jwtAuthenticationFilter.secretKey = "9e7390de9f21809c2a824bd85df9c0505f78cf85741a79e30286e600349b36317a670522b13a3f25f21af8cb261a83178c6f1bc94e3228ce403caf1a1045372b";  // Set a dummy secret key

        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);

        // Mock the request URI
        URI mockUri = mock(URI.class);
        when(mockUri.getPath()).thenReturn("/some-path");
        when(request.getURI()).thenReturn(mockUri);  // Ensure getURI() returns a valid URI object

        when(response.setComplete()).thenReturn(Mono.empty());  // Mock response completion
    }

    private String createToken(long expirationTime) {
        return Jwts
                .builder()
                .signWith(Keys.hmacShaKeyFor(jwtAuthenticationFilter.secretKey.getBytes()))
                .claim("authorities", "ROLE_USER")
                .subject("test")
                .issuer("com.rmhy")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .compact();
    }

    @Test
    public void testValidToken() {
        // Arrange
        String token = createToken(60000);  // Token valid for 60 seconds
        when(request.getHeaders()).thenReturn(HttpHeaders.writableHttpHeaders(new HttpHeaders()));
        exchange.getRequest().getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, chain);

        // Assert
        verify(chain, times(1)).filter(exchange);  // Ensure the chain is called (indicating the token is valid)
    }

    @Test
    public void testExpiredToken() {
        // Arrange
        String token = createToken(-60000);  // Expired token (expired 60 seconds ago)
        when(request.getHeaders()).thenReturn(HttpHeaders.writableHttpHeaders(new HttpHeaders()));
        exchange.getRequest().getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        // No need to mock return for setStatusCode, just verify it later
        when(response.setComplete()).thenReturn(Mono.empty());  // Mock the return value to Mono<Void>

        // Act
        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, chain);

        // Assert
        verify(response, times(1)).setStatusCode(HttpStatus.UNAUTHORIZED);  // Verify setStatusCode is called with UNAUTHORIZED
        verify(response, times(1)).setComplete();  // The response is completed with 401
    }

    @Test
    public void testMissingToken() {
        // Arrange
        when(request.getHeaders()).thenReturn(HttpHeaders.writableHttpHeaders(new HttpHeaders()));
//        when(response.setStatusCode(HttpStatus.UNAUTHORIZED)).thenReturn(response);
        when(response.setComplete()).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, chain);

        // Assert
        verify(response, times(1)).setStatusCode(HttpStatus.UNAUTHORIZED);  // Unauthorized for missing token
        verify(response, times(1)).setComplete();  // The response is completed with 401
    }

    @Test
    public void testInvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.here";
        when(request.getHeaders()).thenReturn(HttpHeaders.writableHttpHeaders(new HttpHeaders()));
        exchange.getRequest().getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken);
//        when(response.setStatusCode(HttpStatus.UNAUTHORIZED)).thenReturn(response);
        when(response.setComplete()).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, chain);

        // Assert
        verify(response, times(1)).setStatusCode(HttpStatus.UNAUTHORIZED);  // Unauthorized for invalid token
        verify(response, times(1)).setComplete();  // The response is completed with 401
    }
}
