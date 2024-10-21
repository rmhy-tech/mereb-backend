package com.rmhy.userservice.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmhy.userservice.controller.v3.AuthControllerV3;
import com.rmhy.userservice.dto.request.RegisterRequest;
import com.rmhy.userservice.dto.response.v3.AuthResponseV3;
import com.rmhy.userservice.model.Role;
import com.rmhy.userservice.service.v3.AuthServiceV3;
import com.rmhy.userservice.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthControllerV3.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class AuthControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthServiceV3 authService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    public void testRegisterUser_SuccessfulValidation() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("test");
        registerRequest.setLastName("user");
        registerRequest.setUsername("valid-test-user");
        registerRequest.setEmail("validtestuser@example.com");
        registerRequest.setPassword("password1234");
        registerRequest.setRole(Role.USER);

        AuthResponseV3 authResponse = new AuthResponseV3("token1234", "refreshToken1234");

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v3/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());

        verify(authService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    public void testRegisterUser_FailedValidation_MissingFirstName() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setLastName("user");
        registerRequest.setUsername("valid-test-user");
        registerRequest.setEmail("testuser@example.com");
        registerRequest.setPassword("password1234");
        registerRequest.setRole(Role.USER);


        AuthResponseV3 authResponse = new AuthResponseV3("token1234", "refreshToken1234");

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);


        mockMvc.perform(post("/api/v3/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("First name is mandatory"));
    }

    @Test
    public void testRegisterUser_FailedValidation_InvalidEmail() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("test");
        registerRequest.setLastName("user");
        registerRequest.setUsername("valid-test-user");
        registerRequest.setEmail("valid-test-user");
        registerRequest.setPassword("password1234");
        registerRequest.setRole(Role.USER);

        AuthResponseV3 authResponse = new AuthResponseV3("token1234", "refreshToken1234");

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v3/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Email should be valid"));
    }
}
