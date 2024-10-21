package com.rmhy.postservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Table(name = "mereb_posts")
@Data
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Content is mandatory")
    @Size(min = 1, max = 300, message = "Content must be between 1 and 300 characters")
    private String content;

    @NotNull(message = "User ID is mandatory")
    private Long userId;

    @NotBlank(message = "Username is mandatory")
    private String username;

    @CreationTimestamp
    @Column(updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    public Post(String content, Long userId, String username) {
        this.content = content;
        this.userId = userId;
        this.username = username;
    }
}
