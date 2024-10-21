package com.rmhy.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmhy.postservice.dto.PostRequest;
import com.rmhy.postservice.dto.PostResponse;
import com.rmhy.postservice.exception.PostNotFoundException;
import com.rmhy.postservice.model.Post;
import com.rmhy.postservice.service.PostService;
import com.rmhy.postservice.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    public void testCreatePost() throws Exception {
        PostRequest request = new PostRequest(1L, "test_user", "Test content");

        PostResponse response = new PostResponse();
        response.setId(1L);
        response.setUserId(1L);
        response.setUsername("test_user");
        response.setContent("Controller test content");
        response.setCreatedAt(ZonedDateTime.now());
        response.setUpdatedAt(ZonedDateTime.now());

        String token = "token1234";

        when(jwtUtil.getTokenFromRequest(any(HttpServletRequest.class))).thenReturn(token);
        when(postService.create(any(PostRequest.class), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/v1/posts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.userId").value(response.getUserId().toString()))
                .andExpect(jsonPath("$.username").value(response.getUsername()))
                .andExpect(jsonPath("$.content").value(response.getContent()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());

        verify(postService, times(1)).create(any(PostRequest.class), anyString());
    }

    @Test
    public void testGetPosts() throws Exception {
        PostResponse response = new PostResponse();
        response.setId(2L);
        response.setUserId(1L);
        response.setUsername("test_user");
        response.setContent("Controller test content");
        response.setCreatedAt(ZonedDateTime.now());
        response.setUpdatedAt(ZonedDateTime.now());

        Page<PostResponse> postPage = new PageImpl<>(List.of(response));

        int page = 0;
        int size = 10;
        String sortBy = "createdAt";
        String sortDirection = "desc";
        when(postService.getPosts(page, size, sortBy, sortDirection)).thenReturn(postPage);
        mockMvc.perform(get("/api/v1/posts")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sortBy", sortBy)
                        .param("sortDirection", sortDirection)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
        verify(postService, times(1)).getPosts(page, size, sortBy, sortDirection);
    }

    @Test
    public void testGetPostsByUser() throws Exception {
        PostResponse response = new PostResponse();
        response.setId(3L);
        response.setUserId(1L);
        response.setUsername("test_user");
        response.setContent("Controller test content");
        response.setCreatedAt(ZonedDateTime.now());
        response.setUpdatedAt(ZonedDateTime.now());

        Page<PostResponse> postPage = new PageImpl<>(List.of(response));

        int page = 0;
        int size = 10;
        String sortBy = "createdAt";
        String sortDirection = "desc";
        when(postService.getPostsByUser(1L, page, size, sortBy, sortDirection)).thenReturn(postPage);
        mockMvc.perform(get("/api/v1/posts/users/1")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sortBy", sortBy)
                        .param("sortDirection", sortDirection)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
        verify(postService, times(1)).getPostsByUser(1L, page, size, sortBy, sortDirection);
    }

    @Test
    public void testDeletePost() throws Exception {
        String response = "Post Deleted";

        when(postService.delete(4L)).thenReturn(response);
        mockMvc.perform(delete("/api/v1/posts/4"))
                .andExpect(status().isOk())
                .andExpect(content().string(response));

        verify(postService, times(1)).delete(4L);
    }

    @Test
    public void testDeletePost_NotFound() throws Exception {
        when(postService.delete(4L)).thenThrow(new PostNotFoundException("Post not found"));
        mockMvc.perform(delete("/api/v1/posts/4"))
                .andExpect(status().isNotFound());
    }

}
