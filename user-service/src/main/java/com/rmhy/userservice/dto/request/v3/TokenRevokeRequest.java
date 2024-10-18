package com.rmhy.userservice.dto.request.v3;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenRevokeRequest {
    private String refreshToken;
}
