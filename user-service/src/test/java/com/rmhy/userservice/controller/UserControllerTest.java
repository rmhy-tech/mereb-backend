package com.rmhy.userservice.controller;

import com.rmhy.userservice.controller.v2.UserControllerV2;
import com.rmhy.userservice.dto.response.UserResponse;
import com.rmhy.userservice.exception.UserNotFoundException;
import com.rmhy.userservice.model.Role;
import com.rmhy.userservice.service.v2.UserServiceV2;
import com.rmhy.userservice.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserControllerV2.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceV2 userServiceV2;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    public void testGetUserByUsername() throws Exception {
        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setFirstName("test");
        response.setLastName("user");
        response.setUsername("test_user");
        response.setEmail("test_user@example.com");
        response.setRole(Role.USER);
        response.setCreatedDate(ZonedDateTime.now());
        response.setUpdatedDate(ZonedDateTime.now());

        when(userServiceV2.getUserByUsername(anyString())).thenReturn(Optional.of(response));

        mockMvc.perform(get("/api/v2/users/test_user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(response.getLastName()))
                .andExpect(jsonPath("$.username").value(response.getUsername()))
                .andExpect(jsonPath("$.email").value(response.getEmail()))
                .andExpect(jsonPath("$.role").value(response.getRole().toString()))
                .andExpect(jsonPath("$.createdDate").isNotEmpty())
                .andExpect(jsonPath("$.updatedDate").isNotEmpty());

        verify(userServiceV2, times(1)).getUserByUsername(anyString());
    }

    @Test
    public void testGetUserByUsername_WithNonExistentUser() throws Exception {
        when(userServiceV2.getUserByUsername(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v2/users/test_user"))
                .andExpect(status().isNotFound());

        verify(userServiceV2, times(1)).getUserByUsername(anyString());
    }

    @Test
    public void testGetAllUsers() throws Exception {
        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setFirstName("test");
        response.setLastName("user");
        response.setUsername("test_user");
        response.setEmail("test_user@example.com");
        response.setRole(Role.USER);
        response.setCreatedDate(ZonedDateTime.now());
        response.setUpdatedDate(ZonedDateTime.now());

        Page<UserResponse> userResponsePage = new PageImpl<>(List.of(response));

        int page = 0;
        int size = 10;
        String sortBy = "updatedDate";
        String sortDirection = "desc";

        when(userServiceV2.getAllUsers(page, size, sortBy, sortDirection)).thenReturn(userResponsePage);

        mockMvc.perform(get("/api/v2/users")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sortBy", sortBy)
                        .param("sortDirection", sortDirection)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(userServiceV2, times(1)).getAllUsers(page, size, sortBy, sortDirection);
    }

    @Test
    public void testDeleteUser() throws Exception {
        when(userServiceV2.deleteUser(anyLong())).thenReturn("User Deleted");

        mockMvc.perform(delete("/api/v2/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User Deleted"));

        verify(userServiceV2, times(1)).deleteUser(anyLong());
    }

    @Test
    public void testDeleteUser_WithNonExistentUser() throws Exception {
        when(userServiceV2.deleteUser(anyLong())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(delete("/api/v2/users/1"))
                .andExpect(status().isNotFound());

        verify(userServiceV2, times(1)).deleteUser(anyLong());
    }
}
