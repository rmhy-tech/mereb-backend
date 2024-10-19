package com.rmhy.postservice.util;

import com.rmhy.postservice.dto.UserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${jwt.secretKey}")
    private String SECRET;

    @Value("${user-service.get-user.url}")
    private String getUserUrl;

    private final RestTemplate restTemplate;

    public Long getUserIdFromUsername(String username, String token) {
        String url = getUserUrl + "/" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UserDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, UserDTO.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            UserDTO userDTO = response.getBody();
            assert userDTO != null;
            return userDTO.getId();
        } else {
            throw new UsernameNotFoundException("User not found");
        }
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



    public String getUsernameFromToken(String token) {
        if(token != null) {
            return getClaimsFromToken(token).getSubject();
        }
        return null;
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new IllegalArgumentException("Token not found");
    }

    public String getUsernameFromRequest(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if(token != null) {
            return getUsernameFromToken(token);
        }
        return null;
    }
}
