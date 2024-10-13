package com.rmhy.userservice;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private static String token;

    @Test
    @Order(1)
    public void testRegisterUser() throws Exception {
        String registerJson = "{ \"firstName\": \"test\",\"lastName\": \"user\",\"username\": \"testuser\",\"email\": \"testuser@email.com\",\"password\": \"password123\", \"role\": \"USER\" }";

        mockMvc.perform(post("/api/v2/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @Order(2)
    public void testRegisterUserWithMissingFields() throws Exception {
        String registerJson = "{ \"username\": \"\", \"password\": \"password123\" }"; // Missing important fields

        mockMvc.perform(post("/api/v2/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    public void testLoginUser() throws Exception {
        String loginJson = "{ \"username\": \"testuser\", \"password\": \"password123\" }";

        MvcResult result = mockMvc.perform(post("/api/v2/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        token = JsonPath.read(response, "$.token");
    }

    @Test
    @Order(4)
    public void testLoginUserWithInvalidCredentials() throws Exception {
        String loginJson = "{ \"username\": \"testuser\", \"password\": \"wrongpassword\" }";

        mockMvc.perform(post("/api/v2/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(5)
    public void testGetAllUsersWithAuth() throws Exception {
        mockMvc.perform(get("/api/v2/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(6)
    public void testGetAllUsersWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v2/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(7)
    public void testGetUserWithAuth() throws Exception {
        mockMvc.perform(get("/api/v2/users/testuser")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    @Order(8)
    public void testGetNonExistentUserWithAuth() throws Exception {
        mockMvc.perform(get("/api/v2/users/nonexistentuser")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound()); // Should return 404 if the user is not found
    }

    @Test
    @Order(9)
    public void testDeleteNonExistentUser() throws Exception {
        mockMvc.perform(delete("/api/v2/users/999") // Assuming 999 is an invalid/non-existent user ID
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound()); // Should return 404 if the user is not found
    }

    @Test
    @Order(11)
    public void testDeleteUserWithoutAuth() throws Exception {
        mockMvc.perform(delete("/api/v2/users/1"))
                .andExpect(status().isForbidden()); // Should return 401 Unauthorized
    }

    @Test
    @Order(10)
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/v2/users/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
