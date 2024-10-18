package com.rmhy.apigateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

@Configuration
public class GatewayConfig {

    @Bean
    public WebFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}
