package dev.rohankumar.supportportal.exception;

import dev.rohankumar.supportportal.model.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Exception
    public ResponseEntity<HttpResponse> handleAccountDisabledException(){

    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus,String message){
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setHttpStatusCode(httpStatus.value());
        httpResponse.setHttpStatus(httpStatus);
        httpResponse.setReason(httpResponse.getReason().toUpperCase());
        httpResponse.setMessage(message.toUpperCase());
        return new ResponseEntity<>(httpResponse,httpStatus);
    }
}
