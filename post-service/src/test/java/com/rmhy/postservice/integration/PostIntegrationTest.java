package com.rmhy.postservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmhy.postservice.config.SecurityTestConfig;
import com.rmhy.postservice.dto.PostRequest;
import com.rmhy.postservice.dto.PostResponse;
import com.rmhy.postservice.dto.UserDTO;
import com.rmhy.postservice.service.PostService;
import com.rmhy.postservice.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(SecurityTestConfig.class)
class PostIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostService postService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private TestRestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @Value("${user-service.get-user.url}")
    private String getUserUrl;

    @BeforeEach
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate.getRestTemplate());
    }

    @Test
    public void testCreatePostWithOutUserIdAndUsername() throws Exception {
        String mockToken = "mockToken";
        when(jwtUtil.getTokenFromRequest(any(HttpServletRequest.class))).thenReturn(mockToken);
        when(jwtUtil.getUsernameFromToken(mockToken)).thenReturn("mockUsername");

        UserDTO mockUserDTO = new UserDTO();
        mockUserDTO.setId(123L);
        mockUserDTO.setFirstName("mockFirstname");
        mockUserDTO.setLastName("mockLastname");
        mockUserDTO.setUsername("mockUsername");
        mockUserDTO.setEmail("mockEmail");
        mockUserDTO.setRole("USER");

        mockServer.expect(ExpectedCount.once(),
                        requestTo(getUserUrl + "/mockUsername"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(new ObjectMapper().writeValueAsString(mockUserDTO), MediaType.APPLICATION_JSON));

        // Request without username or id
        PostRequest request = new PostRequest("Test content");

        mockMvc.perform(post("/api/v1/posts")
                        .header("Authorization", "Bearer " + mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.username").isNotEmpty())
                .andExpect(jsonPath("$.content").value(request.getContent()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
    }

    @Test
    public void testCreatePostWithUsernameAndWithoutUserId() throws Exception {
        String mockToken = "mockToken";
        when(jwtUtil.getTokenFromRequest(any(HttpServletRequest.class))).thenReturn(mockToken);

        UserDTO mockUserDTO = new UserDTO();
        mockUserDTO.setId(123L);
        mockUserDTO.setFirstName("mockFirstname");
        mockUserDTO.setLastName("mockLastname");
        mockUserDTO.setUsername("mockUsername");
        mockUserDTO.setEmail("mockEmail");
        mockUserDTO.setRole("USER");

        mockServer.expect(ExpectedCount.once(),
                        requestTo(getUserUrl + "/mockUsername"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(new ObjectMapper().writeValueAsString(mockUserDTO), MediaType.APPLICATION_JSON));

        PostRequest request = new PostRequest("Test content");
        request.setUsername("mockUsername");

        mockMvc.perform(post("/api/v1/posts")
                        .header("Authorization", "Bearer " + mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.username").isNotEmpty())
                .andExpect(jsonPath("$.content").value(request.getContent()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
    }

    @Test
    public void testCreatePostWithUsernameAndUserId() throws Exception {
        String mockToken = "mockToken";
        when(jwtUtil.getTokenFromRequest(any(HttpServletRequest.class))).thenReturn(mockToken);

        PostRequest request = new PostRequest("Test content");
        request.setUserId(123L);
        request.setUsername("mockUsername");

        mockMvc.perform(post("/api/v1/posts")
                        .header("Authorization", "Bearer " + mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.username").isNotEmpty())
                .andExpect(jsonPath("$.content").value(request.getContent()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
    }

    @Test
    public void testGetPosts() throws Exception {
        int page = 0;
        int size = 10;
        String sortBy = "createdAt";
        String sortDirection = "desc";

        mockMvc.perform(get("/api/v1/posts")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sortBy", sortBy)
                        .param("sortDirection", sortDirection)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    public void testGetPostsByUserID() throws Exception {
        int page = 0;
        int size = 10;
        String sortBy = "createdAt";
        String sortDirection = "desc";

        mockMvc.perform(get("/api/v1/posts/users/123")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sortBy", sortBy)
                        .param("sortDirection", sortDirection)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    public void testGetPostByPostID() throws Exception {
        PostRequest request = new PostRequest(123L, "test_user", "Test content");

        PostResponse response = postService.create(request, "mockToken");

        mockMvc.perform(get("/api/v1/posts/" + response.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.username").isNotEmpty())
                .andExpect(jsonPath("$.content").value(request.getContent()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
    }

    @Test
    public void testGetPostByPostID_InvalidID() throws Exception {
        mockMvc.perform(get("/api/v1/posts/1234"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeletePost() throws Exception {
        PostRequest request = new PostRequest(123L, "test_user", "Test content");

        PostResponse response = postService.create(request, "mockToken");

        mockMvc.perform(delete("/api/v1/posts/" + response.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("Post Deleted"));
    }

    @Test
    public void testDeletePost_NotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/4"))
                .andExpect(status().isNotFound());
    }
}
