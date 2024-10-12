package com.rmhy.userservice.controller.v2;

import com.rmhy.userservice.dto.response.UserResponse;
import com.rmhy.userservice.service.v2.UserServiceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/users")
@RequiredArgsConstructor
public class UserControllerV2 {

    private final UserServiceV2 userServiceV2;

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        UserResponse user = userServiceV2.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        String resp = userServiceV2.deleteUser(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Page<UserResponse> usersPage = userServiceV2.getAllUsers(page, size, sortBy, sortDirection);

        return ResponseEntity.ok(usersPage);
    }
}
