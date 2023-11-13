package com.trendyol.checkout.exception;

import com.trendyol.checkout.models.dto.response.BasicResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CartExceptionHandler {
    @ExceptionHandler(InvalidCartItemException.class)
    public ResponseEntity<BasicResponse> handle(InvalidCartItemException ex) {
        BasicResponse response = new BasicResponse(ex.getMessage(), false);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCartStateException.class)
    public ResponseEntity<BasicResponse> handle(InvalidCartStateException ex) {
        BasicResponse response = new BasicResponse(ex.getMessage(), false);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<BasicResponse> handle(ItemNotFoundException ex) {
        BasicResponse response = new BasicResponse(ex.getMessage(), false);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BasicResponse> handle(IllegalArgumentException ex) {
        BasicResponse response = new BasicResponse(ex.getMessage(), false);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BasicResponse> handle(Exception ex) {
        BasicResponse response = new BasicResponse(ex.getMessage(), false);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
