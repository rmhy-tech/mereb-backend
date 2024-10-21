package com.rmhy.userservice.controller.v3;

import com.rmhy.userservice.dto.request.LoginRequest;
import com.rmhy.userservice.dto.request.RegisterRequest;
import com.rmhy.userservice.dto.request.v3.TokenRefreshRequest;
import com.rmhy.userservice.dto.request.v3.TokenRevokeRequest;
import com.rmhy.userservice.dto.response.v3.AuthResponseV3;
import com.rmhy.userservice.dto.response.v3.TokenRefreshResponse;
import com.rmhy.userservice.service.v3.AuthServiceV3;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v3/auth")
@RequiredArgsConstructor
public class AuthControllerV3 {

    private final AuthServiceV3 authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseV3> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponseV3 response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseV3> login(@Valid @RequestBody LoginRequest request) {
        AuthResponseV3 response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody TokenRevokeRequest request) {
        authService.logout(request);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/logout-all")
    public ResponseEntity<String> logoutAll(@RequestParam Long userId) {
        authService.logoutAll(userId);
        return ResponseEntity.ok("Logged out successfully");
    }

}
