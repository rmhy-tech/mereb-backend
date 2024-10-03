package com.rmhy.postservice.util;

import com.rmhy.postservice.config.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtService jwtService;

    public String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public String getUsernameFromRequest(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if(token != null) {
            return jwtService.getUserNameFromToken(token);
        }
        return null;
    }
}
