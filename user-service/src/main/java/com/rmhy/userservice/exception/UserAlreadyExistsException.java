package com.rmhy.userservice.exception;

public class UserAlreadyExists extends RuntimeException {
  public UserAlreadyExists(String message) {
    super(message);
  }
}
