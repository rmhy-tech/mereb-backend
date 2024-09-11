package com.rmhy.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String content;
    private Long userId;
    private LocalDateTime createdAt;
}
