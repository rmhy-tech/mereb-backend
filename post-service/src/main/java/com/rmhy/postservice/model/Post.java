package com.rmhy.postservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;

    public Post(String content, Long userId, String username, LocalDateTime createdAt) {
        this.content = content;
        this.userId = userId;
        this.username = username;
        this.createdAt = createdAt;
    }
}
