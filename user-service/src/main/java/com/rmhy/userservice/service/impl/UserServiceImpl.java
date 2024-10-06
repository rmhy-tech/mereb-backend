package com.rmhy.userservice.service.impl;

import com.rmhy.userservice.config.JwtService;
import com.rmhy.userservice.dto.request.AuthRequest;
import com.rmhy.userservice.dto.request.RegisterRequest;
import com.rmhy.userservice.dto.response.AuthResponse;
import com.rmhy.userservice.dto.response.UserResponse;
import com.rmhy.userservice.mapper.UserMapper;
import com.rmhy.userservice.model.User;
import com.rmhy.userservice.repository.UserRepository;
import com.rmhy.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        User newUser = mapper.toEntity(request);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = repository.save(newUser);

        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        var user = userDetailsService.loadUserByUsername(request.getUsername());

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }

    @Override
    public Optional<UserResponse> getUserByUsername(String username) {
        Optional<User> found = repository.findByUsername(username);
        if (found.isPresent()) {
            UserResponse response = mapper.toDto(found.get());
            return Optional.of(response);
        }
        return Optional.empty();
    }
}
