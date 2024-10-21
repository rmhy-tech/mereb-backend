package com.rmhy.userservice.repository;

import com.rmhy.userservice.model.RefreshToken;
import com.rmhy.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findAllByUser(User user);
}
