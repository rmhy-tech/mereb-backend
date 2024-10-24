package com.rmhy.postservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostRequest {

    private Long userId;
    private String username;

    @NotBlank(message = "Content is mandatory")
    @Size(min = 1, max = 300, message = "Content must be between 1 and 300 characters")
    private String content;

    public PostRequest(String content) {
        this.content = content;
    }
}
