package dev.rohankumar.supportportal.exception;

import com.auth0.jwt.exceptions.TokenExpiredException;
import dev.rohankumar.supportportal.exception.domain.*;
import dev.rohankumar.supportportal.model.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Objects;

import static dev.rohankumar.supportportal.constant.ExceptionConstant.*;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ExceptionHandling implements ErrorController{

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> handleAccountDisabledException(){
        return createHttpResponse(BAD_REQUEST, ACCOUNT_DISABLED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> handleBadCredentialsException(){
        return createHttpResponse(BAD_REQUEST, INCORRECT_CREDENTIALS);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> handleAccessDeniedException(){
        return createHttpResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> handleLockedException(){
        return createHttpResponse(UNAUTHORIZED, ACCOUNT_LOCKED);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> handleTokenExpiredException(TokenExpiredException ex){
        return createHttpResponse(UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<HttpResponse> handleEmailExistException(EmailExistException ex){
        return createHttpResponse(BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<HttpResponse> handleUsernameExistException(UsernameExistException ex){
        return createHttpResponse(BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> handleEmailNotFoundException(EmailNotFoundException ex){
        return createHttpResponse(BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> handleUserNotFoundException(UserNotFoundException ex){
        return createHttpResponse(BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex){
        HttpMethod supportedMethod = Objects.requireNonNull(ex.getSupportedHttpMethods()).iterator().next();
        return createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED,supportedMethod));
    }

    @ExceptionHandler(NotAnImageFileException.class)
    public ResponseEntity<HttpResponse> handleNotAnImageFileException(NotAnImageFileException exception) {
        LOG.error(exception.getMessage());
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpResponse> handleIOException(IOException ex){
        LOG.error(ex.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR,ERROR_PROCESSING_FILE);
    }

    @RequestMapping(ERROR_PATH)
    public ResponseEntity<HttpResponse> handleEndPointNotFound(){
        return createHttpResponse(NOT_FOUND,"There is no mapping for this url");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> handleInternalServerErrorException(Exception ex){
        LOG.error(ex.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR,INTERNAL_SERVER_ERROR_MESSAGE);
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse> handleNotFoundException(NoResultException ex){
        LOG.error(ex.getMessage());
        return createHttpResponse(NOT_FOUND,ex.getMessage());
    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus,String message){
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setHttpStatusCode(httpStatus.value());
        httpResponse.setHttpStatus(httpStatus);
        httpResponse.setReason(httpStatus.getReasonPhrase().toUpperCase());
        httpResponse.setMessage(message.toUpperCase());
        return new ResponseEntity<>(httpResponse,httpStatus);
    }
}
