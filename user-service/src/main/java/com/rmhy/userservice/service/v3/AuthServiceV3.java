package com.rmhy.userservice.service.v3;

import com.rmhy.userservice.dto.request.v3.TokenRevokeRequest;
import com.rmhy.userservice.dto.request.LoginRequest;
import com.rmhy.userservice.dto.request.RegisterRequest;
import com.rmhy.userservice.dto.request.v3.TokenRefreshRequest;
import com.rmhy.userservice.dto.response.v3.AuthResponseV3;
import com.rmhy.userservice.dto.response.v3.TokenRefreshResponse;
import com.rmhy.userservice.exception.UserAlreadyExistsException;
import com.rmhy.userservice.exception.UserNotFoundException;
import com.rmhy.userservice.mapper.UserMapper;
import com.rmhy.userservice.model.User;
import com.rmhy.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceV3 {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtServiceV3 jwtServiceV3;

    public AuthResponseV3 register(RegisterRequest request) {
        User newUser = mapper.toEntity(request);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        Optional<User> existingUserByUsername = userRepository.findByUsername(newUser.getUsername());
        if (existingUserByUsername.isPresent())
            throw new UserAlreadyExistsException("User with username '"+newUser.getUsername()+"' already exists!");

        Optional<User> existingUserByEmail = userRepository.findByEmail(newUser.getEmail());
        if (existingUserByEmail.isPresent())
            throw new UserAlreadyExistsException("User with email '"+newUser.getEmail()+"' already exists!");

        User savedUser = userRepository.save(newUser);

        String accessToken = jwtServiceV3.generateAccessToken(savedUser);
        String refreshToken = jwtServiceV3.generateRefreshToken(savedUser);

        return new AuthResponseV3(accessToken, refreshToken);
    }

    public AuthResponseV3 login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        var user = userDetailsService.loadUserByUsername(request.getUsername());

        String accessToken = jwtServiceV3.generateAccessToken((User) user);
        String refreshToken = jwtServiceV3.generateRefreshToken((User) user);

        return new AuthResponseV3(accessToken, refreshToken);
    }

    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String accessToken = jwtServiceV3.refreshAccessToken(request.getRefreshToken());
        return new TokenRefreshResponse(accessToken);
    }

    public void logout(TokenRevokeRequest tokenRevokeRequest) {
        jwtServiceV3.revokeToken(tokenRevokeRequest.getRefreshToken());
    }

    public void logoutAll(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        jwtServiceV3.revokeAllTokensForUser(user);
    }

}
