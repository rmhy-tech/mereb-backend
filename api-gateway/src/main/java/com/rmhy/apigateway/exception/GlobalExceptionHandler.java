package com.rmhy.apigateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle 401 Unauthorized for authentication exceptions
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException e) {
        Map<String, String> errorDetails = new HashMap<>();

        if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            errorDetails.put("message", "Unauthorized access: " + e.getMessage());
            return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
        } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
            errorDetails.put("message", "Resource not found: " + e.getMessage());
            return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
        }

        // Default behavior for other exceptions
        errorDetails.put("message", "An error occurred: " + e.getMessage());
        return new ResponseEntity<>(errorDetails, e.getStatusCode());
    }

    // Handle all other general exceptions (default 500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("message", "An unexpected error occurred: " + e.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
