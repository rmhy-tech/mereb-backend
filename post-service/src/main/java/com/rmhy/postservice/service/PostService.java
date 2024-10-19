package com.rmhy.postservice.service;

import com.rmhy.postservice.dto.PostRequest;
import com.rmhy.postservice.dto.PostResponse;
import com.rmhy.postservice.exception.PostNotFoundException;
import com.rmhy.postservice.mapper.PostMapper;
import com.rmhy.postservice.model.Post;
import com.rmhy.postservice.repository.PostRepository;
import com.rmhy.postservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final JwtUtil jwtUtil;

    public PostResponse create(PostRequest request, String token) {
        if (request.getUsername() == null) {
            String username = jwtUtil.getUsernameFromToken(token);
            request.setUsername(username);
        }

        if (request.getUserId() == null) {
            Long userId = jwtUtil.getUserIdFromUsername(request.getUsername(), token);
            request.setUserId(userId);
        }

        Post newPost = postMapper.toEntity(request);
        Post savedPost = postRepository.save(newPost);

        return postMapper.toDto(savedPost);
    }

    public Page<PostResponse> getPostsByUser(Long userId, int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Post> postPage = postRepository.findPostsByUserId(userId, pageable);

        return postPage.map(postMapper::toDto);
    }

    public Page<PostResponse> getPosts(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Post> postPage = postRepository.findAll(pageable);

        return postPage.map(postMapper::toDto);
    }

    public String delete(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            postRepository.delete(post.get());
            return "Post Deleted";
        }
        throw new PostNotFoundException("Post not found");
    }

}
