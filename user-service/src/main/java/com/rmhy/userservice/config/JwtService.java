package com.rmhy.userservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtService {
    @Value("${jwt.secretKey}")
    private String SECRET;

    public String generateToken(UserDetails userDetails) {
        return Jwts
                .builder()
                .signWith(signKey())
                .claim("authorities", populateAuthorities(userDetails.getAuthorities()))
                .subject(userDetails.getUsername())
                .issuer("com.rmhy")
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + 1000 * 24 * 60 * 60))
                .compact();
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    private SecretKey signKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(signKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
