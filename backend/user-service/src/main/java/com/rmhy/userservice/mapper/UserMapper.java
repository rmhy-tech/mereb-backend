package com.rmhy.userservice.mapper;

import com.rmhy.userservice.dto.request.RegisterRequest;
import com.rmhy.userservice.dto.response.UserResponse;
import com.rmhy.userservice.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest request) {
        return new User(
                request.getFirstName(),
                request.getLastName(),
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
        );
    }

    public RegisterRequest toRegisterDto(User user) {
        return new RegisterRequest(
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );
    }

    public UserResponse toDto(User user) {
        return new UserResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );
    }
}
