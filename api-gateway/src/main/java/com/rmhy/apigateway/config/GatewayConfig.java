package com.rmhy.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class GatewayConfig {

    @Value("${CORS_ALLOWED_ORIGINS:${cors.allowed.origins}}")
    private String allowedOrigins;

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        List<String> originsList = Arrays.asList(allowedOrigins.split(","));
        List<String> origins = originsList.isEmpty() ? List.of("*") : originsList;
        corsConfig.setAllowedOrigins(origins);

        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

    @Bean
    public GlobalFilter customGlobalFilter() {
        return (exchange, chain) -> chain.filter(exchange.mutate()
                .request(r -> r.headers(headers -> {
                    String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
                    if (authHeader != null) {
                        headers.add("Authorization", authHeader);
                    }
                }))
                .build());
    }
}
