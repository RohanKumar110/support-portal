package dev.rohankumar.supportportal.exception;

public class EmailExistException extends RuntimeException{

    public EmailExistException(String message) {
        super(message);
    }
}
