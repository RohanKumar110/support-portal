package dev.rohankumar.supportportal.exception.domain;

public class EmailExistException extends RuntimeException{

    public EmailExistException(String message) {
        super(message);
    }
}
