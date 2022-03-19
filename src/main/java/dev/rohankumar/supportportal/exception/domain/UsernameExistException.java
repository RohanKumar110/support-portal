package dev.rohankumar.supportportal.exception;

public class UsernameExistException extends RuntimeException{

    public UsernameExistException(String message) {
        super(message);
    }
}
