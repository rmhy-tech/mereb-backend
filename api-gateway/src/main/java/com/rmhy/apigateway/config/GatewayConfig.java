package com.rmhy.apigateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
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
