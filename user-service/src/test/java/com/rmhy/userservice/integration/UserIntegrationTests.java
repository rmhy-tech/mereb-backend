package com.rmhy.userservice.integration;

import com.rmhy.userservice.dto.request.RegisterRequest;
import com.rmhy.userservice.dto.response.UserResponse;
import com.rmhy.userservice.dto.response.v3.AuthResponseV3;
import com.rmhy.userservice.model.Role;
import com.rmhy.userservice.model.User;
import com.rmhy.userservice.repository.UserRepository;
import com.rmhy.userservice.service.v2.UserServiceV2;
import com.rmhy.userservice.service.v3.AuthServiceV3;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("dev")
public class UserIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthServiceV3 authServiceV3;

    @Autowired
    private UserServiceV2 userServiceV2;

    @Test
    public void getUserByUsernameIntegrationTest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("test");
        request.setLastName("user");
        request.setUsername("test_user1");
        request.setEmail("testuser1@example.com");
        request.setPassword("password1234");
        request.setRole(Role.USER);

        AuthResponseV3 response = authServiceV3.register(request);

        mockMvc.perform(get("/api/v2/users/" + request.getUsername())
                        .header("Authorization", "Bearer " + response.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value(request.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(request.getLastName()))
                .andExpect(jsonPath("$.username").value(request.getUsername()))
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.role").value(request.getRole().toString()))
                .andExpect(jsonPath("$.createdDate").isNotEmpty())
                .andExpect(jsonPath("$.updatedDate").isNotEmpty());
    }

    @Test
    public void getAllUsersIntegrationTest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("test");
        request.setLastName("user");
        request.setUsername("test_user2");
        request.setEmail("testuser2@example.com");
        request.setPassword("password1234");
        request.setRole(Role.USER);

        AuthResponseV3 response = authServiceV3.register(request);

        int page = 0;
        int size = 10;
        String sortBy = "updatedDate";
        String sortDirection = "desc";

        mockMvc.perform(get("/api/v2/users")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sortBy", sortBy)
                        .param("sortDirection", sortDirection)
                        .header("Authorization", "Bearer " + response.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Transactional
    public void deleteUserIntegrationTest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("test");
        request.setLastName("user");
        request.setUsername("test_user3");
        request.setEmail("testuser3@example.com");
        request.setPassword("password1234");
        request.setRole(Role.USER);

        AuthResponseV3 response = authServiceV3.register(request);

        Optional<User> createdUser = userRepository.findByUsername(request.getUsername());
        assertTrue(createdUser.isPresent());
        String userId = createdUser.get().getUserId().toString();
        assertNotNull(userId);

        mockMvc.perform(delete("/api/v2/users/" + userId)
                        .header("Authorization", "Bearer " + response.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(content().string("User Deleted"));

        Optional<UserResponse> deletedUser = userServiceV2.getUserByUsername(request.getUsername());
        assertTrue(deletedUser.isEmpty());
    }

}
