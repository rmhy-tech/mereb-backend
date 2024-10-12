package com.rmhy.userservice.service.v2;

import com.rmhy.userservice.dto.response.UserResponse;
import com.rmhy.userservice.mapper.UserMapper;
import com.rmhy.userservice.model.User;
import com.rmhy.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceV2 {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    public Optional<UserResponse> getUserByUsername(String username) {
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            UserResponse response = mapper.toDto(found.get());
            return Optional.of(response);
        }
        return Optional.empty();
    }

    public Optional<String> deleteUser(Long id) {
        Optional<User> found = userRepository.findById(id);
        if (found.isPresent()) {
            userRepository.delete(found.get());
            return Optional.of("User Deleted");
        }
        return Optional.empty();
    }

    public Page<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<User> userPage = userRepository.findAll(pageable);

        return userPage.map(mapper::toDto);
    }
}
