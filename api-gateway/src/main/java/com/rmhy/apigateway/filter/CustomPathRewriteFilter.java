package com.rmhy.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class CustomPathRewriteFilter extends AbstractGatewayFilterFactory<Object> {

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String originalPath = exchange.getRequest().getURI().getPath();

            // Remove '/user-service' or '/post-service' from the path
            String modifiedPath = originalPath.replaceFirst("/(user-service|post-service)", "");

            // Create new request with modified path
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .path(modifiedPath)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }
}
