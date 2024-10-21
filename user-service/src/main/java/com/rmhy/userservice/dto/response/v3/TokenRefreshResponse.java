package com.rmhy.userservice.dto.response.v3;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenRefreshResponse {
    private String newAccessToken;
}
