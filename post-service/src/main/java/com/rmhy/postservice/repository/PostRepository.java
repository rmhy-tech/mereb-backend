package com.rmhy.postservice.repository;

import com.rmhy.postservice.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findPostsByUserId(Long userId, Pageable pageable);
}
