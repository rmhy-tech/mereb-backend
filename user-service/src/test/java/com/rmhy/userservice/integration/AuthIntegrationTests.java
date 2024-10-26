package com.rmhy.userservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.rmhy.userservice.dto.request.LoginRequest;
import com.rmhy.userservice.dto.request.RegisterRequest;
import com.rmhy.userservice.dto.request.v3.TokenRefreshRequest;
import com.rmhy.userservice.dto.request.v3.TokenRevokeRequest;
import com.rmhy.userservice.dto.response.v3.AuthResponseV3;
import com.rmhy.userservice.model.Role;
import com.rmhy.userservice.model.User;
import com.rmhy.userservice.repository.UserRepository;
import com.rmhy.userservice.service.v3.AuthServiceV3;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class AuthIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthServiceV3 authServiceV3;

    @Test
    public void createUserIntegrationTest() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("test");
        registerRequest.setLastName("user");
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("testuser@example.com");
        registerRequest.setPassword("password1234");
        registerRequest.setRole(Role.USER);

        mockMvc.perform(post("/api/v3/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());

        Optional<User> createdUser = userRepository.findByUsername(registerRequest.getUsername());
        assertTrue(createdUser.isPresent());
        assertNotNull(createdUser.get().getUserId());
        assertEquals(registerRequest.getFirstName(), createdUser.get().getFirstName());
        assertEquals(registerRequest.getLastName(), createdUser.get().getLastName());
        assertEquals(registerRequest.getUsername(), createdUser.get().getUsername());
        assertEquals(registerRequest.getEmail(), createdUser.get().getEmail());
        assertEquals(registerRequest.getRole(), createdUser.get().getRole());
        assertNotNull(createdUser.get().getCreatedDate());
        assertNotNull(createdUser.get().getUpdatedDate());
    }

    @Test
    public void loginUserIntegrationTest() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("test1");
        registerRequest.setLastName("user1");
        registerRequest.setUsername("testuser1");
        registerRequest.setEmail("testuser1@example.com");
        registerRequest.setPassword("password1234");
        registerRequest.setRole(Role.USER);
        authServiceV3.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(registerRequest.getUsername());
        loginRequest.setPassword(registerRequest.getPassword());

        mockMvc.perform(post("/api/v3/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    public void refreshTokenIntegrationTest() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("test1");
        registerRequest.setLastName("user1");
        registerRequest.setUsername("testuser1");
        registerRequest.setEmail("testuser1@example.com");
        registerRequest.setPassword("password1234");
        registerRequest.setRole(Role.USER);
        AuthResponseV3 responseV3 = authServiceV3.register(registerRequest);

        TokenRefreshRequest refreshRequest = new TokenRefreshRequest(responseV3.getRefreshToken());

        MvcResult result = mockMvc.perform(post("/api/v3/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newAccessToken").isNotEmpty())
                .andReturn();
        String response = result.getResponse().getContentAsString();
        String newAccessToken = JsonPath.read(response, "$.newAccessToken");

        mockMvc.perform(get("/api/v2/users/"+registerRequest.getUsername())
                        .header("Authorization", "Bearer " + newAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(registerRequest.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(registerRequest.getLastName()))
                .andExpect(jsonPath("$.username").value(registerRequest.getUsername()))
                .andExpect(jsonPath("$.email").value(registerRequest.getEmail()));
    }

    @Test
    public void logoutUserIntegrationTest() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("test1");
        registerRequest.setLastName("user1");
        registerRequest.setUsername("testuser1");
        registerRequest.setEmail("testuser1@example.com");
        registerRequest.setPassword("password1234");
        registerRequest.setRole(Role.USER);
        AuthResponseV3 responseV3 = authServiceV3.register(registerRequest);

        TokenRevokeRequest revokeRequest = new TokenRevokeRequest(responseV3.getRefreshToken());

        mockMvc.perform(post("/api/v3/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(revokeRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));

        TokenRefreshRequest refreshRequest = new TokenRefreshRequest(responseV3.getRefreshToken());

        mockMvc.perform(post("/api/v3/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void logoutAllUsersIntegrationTest() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("test1");
        registerRequest.setLastName("user1");
        registerRequest.setUsername("testuser1");
        registerRequest.setEmail("testuser1@example.com");
        registerRequest.setPassword("password1234");
        registerRequest.setRole(Role.USER);
        AuthResponseV3 responseV3 = authServiceV3.register(registerRequest);

        TokenRevokeRequest revokeRequest = new TokenRevokeRequest(responseV3.getRefreshToken());
        Optional<User> createdUser = userRepository.findByUsername(registerRequest.getUsername());
        assertTrue(createdUser.isPresent());

        mockMvc.perform(post("/api/v3/auth/logout-all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(createdUser.get().getUserId()))
                        .content(objectMapper.writeValueAsString(revokeRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));

        TokenRefreshRequest refreshRequest = new TokenRefreshRequest(responseV3.getRefreshToken());

        mockMvc.perform(post("/api/v3/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isBadRequest());
    }

}
