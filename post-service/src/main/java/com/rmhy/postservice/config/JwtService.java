package com.rmhy.postservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public class JwtService {
    @Value("${jwt.secretKey}")
    private String SECRET;

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

    public String getUserNameFromToken(String token) {
        if(token != null) {
            return getClaimsFromToken(token).getSubject();
        }
        return null;
    }
}
