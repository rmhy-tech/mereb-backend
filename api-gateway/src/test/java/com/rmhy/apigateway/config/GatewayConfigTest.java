package com.rmhy.apigateway.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GatewayConfigTest {

    private GatewayConfig gatewayConfig;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        gatewayConfig = new GatewayConfig();
    }

    @Test
    public void testGlobalFilterBean() {
        GlobalFilter globalFilter = gatewayConfig.customGlobalFilter();
        assertNotNull(globalFilter);  // Ensure the global filter is not null
    }
}
