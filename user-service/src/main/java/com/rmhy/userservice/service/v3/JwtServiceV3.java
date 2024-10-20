package com.rmhy.userservice.service.v3;

import com.rmhy.userservice.exception.InvalidTokenException;
import com.rmhy.userservice.model.RefreshToken;
import com.rmhy.userservice.model.User;
import com.rmhy.userservice.repository.RefreshTokenRepository;
import com.rmhy.userservice.repository.UserRepository;
import com.rmhy.userservice.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtServiceV3 {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public String generateAccessToken(User user) {
        return jwtUtil.generateAccessToken(user);
    }

    public String generateRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken(
                jwtUtil.generateRefreshToken(user),
                new Date(System.currentTimeMillis() + jwtUtil.getRefreshTokenExpirationMs())
        );

        Optional<RefreshToken> tokenEntityOpt = refreshTokenRepository.findByToken(refreshToken.getToken());
        if (tokenEntityOpt.isPresent()) {
            if (!tokenEntityOpt.get().isRevoked() && !isTokenExpired(tokenEntityOpt.get())) {
                return tokenEntityOpt.get().getToken();
            }
            if (tokenEntityOpt.get().isRevoked() && !isTokenExpired(tokenEntityOpt.get())) {
                tokenEntityOpt.get().setRevoked(false);
                refreshTokenRepository.save(tokenEntityOpt.get());
                return tokenEntityOpt.get().getToken();
            }
        }

        user.addRefreshToken(refreshToken);
        userRepository.save(user);
        return refreshToken.getToken();
    }

    public String refreshAccessToken(String refreshToken) {
        Optional<RefreshToken> tokenEntityOpt = refreshTokenRepository.findByToken(refreshToken);
        if (tokenEntityOpt.isPresent()) {
            RefreshToken tokenEntity = tokenEntityOpt.get();
            if (!tokenEntity.isRevoked() && !isTokenExpired(tokenEntity)) {
                return jwtUtil.generateAccessToken(tokenEntity.getUser());
            } else {
                throw new InvalidTokenException("Invalid or expired refresh token.");
            }
        }
        throw new InvalidTokenException("Refresh token not found.");
    }


    public void revokeToken(String refreshToken) {
        Optional<RefreshToken> tokenEntityOpt = refreshTokenRepository.findByToken(refreshToken);
        tokenEntityOpt.ifPresent(token -> {
            if (!token.isRevoked()) {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            }
        });
    }

    public void revokeAllTokensForUser(User user) {
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser(user);
        for (RefreshToken token : tokens) {
            token.setRevoked(true); // Revoke each token
        }
        refreshTokenRepository.saveAll(tokens); // Batch update to revoke all tokens
    }

    // Utility to check if the token is expired
    private boolean isTokenExpired(RefreshToken token) {
        return token.getExpiryDate().before(new Date());
    }
}
