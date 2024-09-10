package com.rmhy.userservice;

import com.rmhy.userservice.dto.request.AuthRequest;
import com.rmhy.userservice.dto.request.RegisterRequest;
import com.rmhy.userservice.dto.response.AuthResponse;
import com.rmhy.userservice.dto.response.UserResponse;
import com.rmhy.userservice.model.Role;
import com.rmhy.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Optional;

@SpringBootApplication
@RequiredArgsConstructor
public class UserServiceApplication implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "Richard",
                "Hendriks",
                "r.hendriks",
                "rhendriks@pp.com",
                "piper1234",
                Role.USER
        );
        AuthRequest authRequest = new AuthRequest("r.hendriks", "piper1234");

        AuthResponse registeredUser = userService.register(registerRequest);
        AuthResponse loggedInUser = userService.login(authRequest);

        Optional<UserResponse> userResponse = userService.getUser("r.hendriks");

        System.out.println("registeredUser: " + registeredUser);
        System.out.println("loggedInUser: " + loggedInUser);
        if (userResponse.isPresent()){
            System.out.println("userResponse: " + userResponse.get());
        } else {
            System.out.println("user not found");
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
