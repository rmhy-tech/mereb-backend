package com.rmhy.postservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    public void requestUserIdFromUsername(String username, String correlationId) {
        // Send the username to the user-service-request topic with the correlation ID as the key
        kafkaTemplate.send("user-service-request", correlationId, username);
    }

}
