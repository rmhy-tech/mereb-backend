package com.rmhy.userservice.service.impl;

import com.rmhy.userservice.dto.request.AuthRequest;
import com.rmhy.userservice.dto.request.RegisterRequest;
import com.rmhy.userservice.dto.response.AuthResponse;
import com.rmhy.userservice.dto.response.UserResponse;
import com.rmhy.userservice.mapper.UserMapper;
import com.rmhy.userservice.model.User;
import com.rmhy.userservice.repository.UserRepository;
import com.rmhy.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public AuthResponse register(RegisterRequest request) {
        User newUser = mapper.toEntity(request);

        User savedUser = repository.save(newUser);

        return new AuthResponse(savedUser.getUsername());
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        Optional<User> found = repository.findByUsername(request.getUsername());
        if (found.isPresent() && found.get().getPassword().equals(request.getPassword())) {
            return new AuthResponse(found.get().getUsername());
        }
        return null;
    }

    @Override
    public Optional<UserResponse> getUser(String username) {
        Optional<User> found = repository.findByUsername(username);
        if (found.isPresent()) {
            UserResponse response = mapper.toDto(found.get());
            return Optional.of(response);
        }
        return Optional.empty();
    }
}
