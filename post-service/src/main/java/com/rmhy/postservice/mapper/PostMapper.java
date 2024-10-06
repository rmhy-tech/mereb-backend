package com.rmhy.postservice.mapper;

import com.rmhy.postservice.dto.PostRequest;
import com.rmhy.postservice.dto.PostResponse;
import com.rmhy.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostMapper {

    public Post toEntity(PostRequest request) {
        return new Post(
                request.getContent(),
                request.getUserId(),
                request.getUsername(),
                request.getCreatedAt()
        );
    }

    public PostResponse toDto(Post post) {
        return new PostResponse(
                post.getId(),
                post.getContent(),
                post.getUserId(),
                post.getUsername(),
                post.getCreatedAt()
        );
    }

    public List<PostResponse> toDto(List<Post> posts) {
        return posts.stream().map(this::toDto).toList();
    }

}
