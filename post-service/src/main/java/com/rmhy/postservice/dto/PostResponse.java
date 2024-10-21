package com.rmhy.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private Long id;
    private String content;
    private Long userId;
    private String username;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
