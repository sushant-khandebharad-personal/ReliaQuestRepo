package com.example.rqchallenge.employees;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@Value(value = "${data.exception.AnyException}")
    private String message1;
	
	
	@Value(value = "${data.exception.HttpClientErrorException.TooManyRequests}")
    private String message3;
	
	
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity anyException(Exception exception) {
        return new ResponseEntity<>(message1, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(value = HttpClientErrorException.TooManyRequests.class)
    public ResponseEntity tooManyRequestsException(HttpClientErrorException.TooManyRequests exception) {
        return new ResponseEntity<>(message3, HttpStatus.TOO_MANY_REQUESTS);
    }
    
           
}
