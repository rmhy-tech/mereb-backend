package com.rmhy.userservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmhy.userservice.repository.UserRepository;
import com.rmhy.userservice.service.v3.AuthServiceV3;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class CorsRequestTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthServiceV3 authServiceV3;

    @Test
    public void shouldAllowCorsFromLocalhost_5173() throws Exception {
        mockMvc.perform(options("/api/v3/auth/register") // Change to your actual endpoint
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PATCH,DELETE,OPTIONS"));
    }

    @Test
    public void shouldAllowCorsFromLocalIP_8085() throws Exception {
        mockMvc.perform(options("/api/v3/auth/register") // Change to your actual endpoint
                        .header(HttpHeaders.ORIGIN, "http://192.168.1.109:8085")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://192.168.1.109:8085"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PATCH,DELETE,OPTIONS"));
    }

    @Test
    public void shouldAllowCorsFromMereb_app() throws Exception {
        mockMvc.perform(options("/api/v3/auth/register") // Change to your actual endpoint
                        .header(HttpHeaders.ORIGIN, "https://mereb.app")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "https://mereb.app"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PATCH,DELETE,OPTIONS"));
    }
}
