package com.rmhy.userservice.service;

import com.rmhy.userservice.dto.request.AuthRequest;
import com.rmhy.userservice.dto.request.RegisterRequest;
import com.rmhy.userservice.dto.response.AuthResponse;
import com.rmhy.userservice.dto.response.UserResponse;

import java.util.Optional;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(AuthRequest request);
    Optional<UserResponse> getUser(String username);
}
