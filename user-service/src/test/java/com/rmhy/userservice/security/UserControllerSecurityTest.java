package com.rmhy.userservice.security;

import com.rmhy.userservice.controller.v2.UserControllerV2;
import com.rmhy.userservice.service.v2.UserServiceV2;
import com.rmhy.userservice.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserControllerV2.class)
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", password = "password1234", roles = "USER")
@ActiveProfiles("test")
public class UserControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserControllerV2 userController;

    @MockBean
    private UserServiceV2 userServiceV2;

    @Test
    public void testAccessSecuredEndpointWithAuthentication() throws Exception {
        mockMvc.perform(get("/api/v2/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "updatedDate")
                        .param("sortDirection", "desc")
                )
                .andExpect(status().isOk());
    }

    @Test
    public void testAccessSecuredEndpointWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v2/users").with(anonymous()))
                .andExpect(status().isUnauthorized());
    }
}
