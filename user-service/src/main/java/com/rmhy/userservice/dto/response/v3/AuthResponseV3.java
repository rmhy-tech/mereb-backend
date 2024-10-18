package com.rmhy.userservice.dto.response.v3;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseV3 {
    private String accessToken;
    private String refreshToken;
}
