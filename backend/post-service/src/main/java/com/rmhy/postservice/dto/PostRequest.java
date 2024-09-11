package com.rmhy.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostRequest {
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
}
