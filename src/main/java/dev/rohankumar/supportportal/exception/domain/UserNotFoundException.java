package dev.rohankumar.supportportal.exception.domain;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String message) {
        super(message);
    }
}
