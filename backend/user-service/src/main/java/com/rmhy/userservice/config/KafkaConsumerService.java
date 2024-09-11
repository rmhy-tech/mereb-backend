package com.rmhy.userservice.config;

import com.rmhy.userservice.model.User;
import com.rmhy.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Optional;

@RequiredArgsConstructor
public class KafkaConsumerService {

    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;

    @KafkaListener(topics = "user-service-request", groupId = "user-group")
    public void handleUserRequest(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            // Create a response payload
            String responsePayload = user.get().getId().toString();

            // Send response to user-service-response topic
            kafkaProducerService.sendMessage("user-service-response", responsePayload);
        }
    }

}
