package com.rmhy.userservice.service.v2;

import com.rmhy.userservice.config.JwtService;
import com.rmhy.userservice.dto.request.LoginRequest;
import com.rmhy.userservice.dto.request.RegisterRequest;
import com.rmhy.userservice.dto.response.AuthResponse;
import com.rmhy.userservice.mapper.UserMapper;
import com.rmhy.userservice.model.User;
import com.rmhy.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final UserMapper mapper;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest request) {
        User newUser = mapper.toEntity(request);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = repository.save(newUser);

        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        var user = userDetailsService.loadUserByUsername(request.getUsername());

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }
}
