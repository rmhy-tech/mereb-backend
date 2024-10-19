package com.rmhy.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ErrorHandlingFilter extends AbstractGatewayFilterFactory<Object> {

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> chain.filter(exchange)
                .then(Mono.defer(() -> {
                    HttpStatusCode statusCode = exchange.getResponse().getStatusCode();

                    // If downstream service returned 500, translate to 502 Bad Gateway
                    assert statusCode != null;
                    if (HttpStatus.INTERNAL_SERVER_ERROR.value() == statusCode.value()) {
                        exchange.getResponse().setStatusCode(HttpStatus.BAD_GATEWAY);
                    }
                    return Mono.empty();
                }));
    }
}
