package com.rmhy.apigateway;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Date;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock()
public class ApiGatewayIntegrationTest {

    @LocalServerPort
    private int port;

    @RegisterExtension
    static WireMockExtension userServiceMock = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(6082))
            .build();

    @RegisterExtension
    static WireMockExtension postServiceMock = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(6083))
            .build();

    @Value("${jwt.secretKey}")
    private String secretKey;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)  // Dynamically set the port
                .build();
    }

    private String createValidToken() {
        return Jwts
                .builder()
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .claim("authorities", "ROLE_USER")
                .subject("testUser")
                .issuer("com.rmhy")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .compact();
    }

    private String createExpiredToken() {
        return Jwts
                .builder()
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .claim("authorities", "ROLE_USER")
                .subject("testUser")
                .issuer("com.rmhy")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() - 60000))
                .compact();
    }

    // -----------------------------------
    // Public Route Tests (No JWT Required)
    // -----------------------------------

    @Test
    public void testPublicRouteNoTokenRequired() {
        // Arrange: Set up WireMock for a public endpoint in the user-service
        userServiceMock.stubFor(get(urlEqualTo("/api/auth/info"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Public Info\"}")
                        .withStatus(200)));

        // Act: Make a request to the public endpoint without a token
        webTestClient.get()
                .uri("/user-service/api/auth/info")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Public Info");

        // Assert: Verify that WireMock received the request
        userServiceMock.verify(getRequestedFor(urlEqualTo("/api/auth/info")));
    }

    @Test
    public void testPublicRouteVersionedNoTokenRequired() {
        // Arrange: Set up WireMock for versioned public endpoint
        userServiceMock.stubFor(get(urlEqualTo("/api/v3/auth/details"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Public V3 Info\"}")
                        .withStatus(200)));

        // Act: Make a request to the versioned public endpoint without a token
        webTestClient.get()
                .uri("/user-service/api/v3/auth/details")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Public V3 Info");

        // Assert: Verify that WireMock received the request
        userServiceMock.verify(getRequestedFor(urlEqualTo("/api/v3/auth/details")));
    }

    // -----------------------------------
    // Protected Route Tests (JWT Required)
    // -----------------------------------

    @Test
    public void testProtectedRouteWithValidToken() {
        // Arrange: Set up WireMock for a protected endpoint in the user-service
        userServiceMock.stubFor(get(urlEqualTo("/protected/data"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Protected Data\"}")
                        .withStatus(200)));

        // Act: Make a request to the protected endpoint with a valid token
        webTestClient.get()
                .uri("/user-service/protected/data")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createValidToken())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Protected Data");

        // Assert: Verify that WireMock received the request
        userServiceMock.verify(getRequestedFor(urlEqualTo("/protected/data")));
    }

    @Test
    public void testProtectedRouteWithoutTokenReturnsUnauthorized() {
        // Arrange: Set up WireMock for a protected endpoint
        userServiceMock.stubFor(get(urlEqualTo("/user-service/protected/data"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Protected Data\"}")
                        .withStatus(200)));

        // Act: Make a request to the protected endpoint without a token
        webTestClient.get()
                .uri("/user-service/protected/data")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void testProtectedRouteWithExpiredTokenReturnsUnauthorized() {
        // Act: Make a request with an expired token
        webTestClient.get()
                .uri("/user-service/protected/data")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createExpiredToken())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // -----------------------------------
    // Edge Case Tests
    // -----------------------------------

    @Test
    public void testInvalidRouteReturnsNotFound() {
        // Act: Make a request to an invalid route
        webTestClient.get()
                .uri("/invalid-service/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createValidToken())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testServiceErrorReturnsBadGateway() {
        // Arrange: Set up WireMock for user-service to return a 500 Internal Server Error
        userServiceMock.stubFor(get(urlEqualTo("/protected/data"))
                .willReturn(aResponse().withStatus(500)));

        // Act: Make a request to the user-service
        webTestClient.get()
                .uri("/user-service/protected/data")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createValidToken())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_GATEWAY);
    }
}
