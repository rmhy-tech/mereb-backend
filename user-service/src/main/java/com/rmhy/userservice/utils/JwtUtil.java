package com.rmhy.userservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    @Value("${jwt.secretKey}")
    private String jwtSecret;

    @Value("${jwt.access.token.expiration}")
    private long accessTokenExpirationMs;

    @Getter
    @Value("${jwt.refresh.token.expiration}")
    private long refreshTokenExpirationMs;

    public String generateAccessToken(UserDetails userDetails) {
        return Jwts
                .builder()
                .signWith(signKey())
                .claim("authorities", populateAuthorities(userDetails.getAuthorities()))
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts
                .builder()
                .signWith(signKey())
                .claim("authorities", populateAuthorities(userDetails.getAuthorities()))
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(signKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    private SecretKey signKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

}
