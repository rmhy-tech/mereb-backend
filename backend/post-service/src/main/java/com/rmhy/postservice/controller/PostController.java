package com.rmhy.postservice.controller;

import com.rmhy.postservice.config.KafkaProducerService;
import com.rmhy.postservice.dto.PostRequest;
import com.rmhy.postservice.dto.PostResponse;
import com.rmhy.postservice.dto.UserDTO;
import com.rmhy.postservice.service.PostService;
import com.rmhy.postservice.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    @Value("${user-service.url}")
    private String userServiceUrl;
    private final PostService postService;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest postRequest, HttpServletRequest request) {
        String username = jwtUtil.getUsernameFromRequest(request);
        Long userId = getUserIdFromUsername(username);
        postRequest.setUserId(userId);
        PostResponse createdPost = postService.create(postRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    private Long getUserIdFromUsername(String username) {
        String url = "http://"+userServiceUrl+"/api/users/" + username; // Replace with your user service URL
        ResponseEntity<UserDTO> response = restTemplate.getForEntity(url, UserDTO.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            UserDTO userDTO = response.getBody();
            assert userDTO != null;
            return userDTO.getId();
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<PostResponse>> getPostsByUser(@PathVariable Long userId) {
        List<PostResponse> posts = postService.getPostsByUser(userId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        postService.getPostsByUser(postId);
        return ResponseEntity.ok("Deleted");
    }
}
