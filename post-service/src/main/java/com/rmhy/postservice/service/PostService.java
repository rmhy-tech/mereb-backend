package com.rmhy.postservice.service;

import com.rmhy.postservice.dto.PostRequest;
import com.rmhy.postservice.dto.PostResponse;
import com.rmhy.postservice.mapper.PostMapper;
import com.rmhy.postservice.model.Post;
import com.rmhy.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostResponse create(PostRequest request) {
        Post newPost = postMapper.toEntity(request);

        Post savedPost = postRepository.save(newPost);

        return postMapper.toDto(savedPost);
    }

    public List<PostResponse> getPostsByUser(Long userId) {
        List<Post> foundPosts = postRepository.findByUserId(userId);
        return postMapper.toDto(foundPosts);
    }

    public List<PostResponse> getPosts() {
        List<Post> foundPosts = postRepository.findAll();
        return postMapper.toDto(foundPosts);
    }

    public void delete(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        post.ifPresent(postRepository::delete);
    }

}
