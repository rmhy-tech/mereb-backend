package com.rmhy.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmhy.userservice.controller.v3.AuthControllerV3;
import com.rmhy.userservice.dto.request.LoginRequest;
import com.rmhy.userservice.dto.request.RegisterRequest;
import com.rmhy.userservice.dto.request.v3.TokenRefreshRequest;
import com.rmhy.userservice.dto.request.v3.TokenRevokeRequest;
import com.rmhy.userservice.dto.response.v3.AuthResponseV3;
import com.rmhy.userservice.dto.response.v3.TokenRefreshResponse;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthControllerV3.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthServiceV3 authService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRegister() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("test");
        request.setLastName("user");
        request.setUsername("test_user");
        request.setEmail("testuser@example.com");
        request.setPassword("password1234");
        request.setRole(Role.USER);

        AuthResponseV3 authResponse = new AuthResponseV3("token1234", "refreshToken1234");

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v3/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("token1234"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken1234"));

        verify(authService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    public void testLogin() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("test_user");
        request.setPassword("password1234");

        AuthResponseV3 authResponse = new AuthResponseV3("token1234", "refreshToken1234");

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v3/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("token1234"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken1234"));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    public void testRefreshToken() throws Exception {
        TokenRefreshRequest refreshRequest = new TokenRefreshRequest("refreshToken123");
        TokenRefreshResponse refreshResponse = new TokenRefreshResponse("newToken123");

        when(authService.refreshToken(any(TokenRefreshRequest.class))).thenReturn(refreshResponse);

        mockMvc.perform(post("/api/v3/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newAccessToken").value("newToken123"));

        verify(authService, times(1)).refreshToken(any(TokenRefreshRequest.class));
    }

    @Test
    public void testLogout() throws Exception {
        TokenRevokeRequest revokeRequest = new TokenRevokeRequest("token123");

        mockMvc.perform(post("/api/v3/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(revokeRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));

        verify(authService, times(1)).logout(any(TokenRevokeRequest.class));
    }

    @Test
    public void testLogoutAll() throws Exception {
        Long userId = 1L;

        mockMvc.perform(post("/api/v3/auth/logout-all")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));

        verify(authService, times(1)).logoutAll(userId);
    }
}