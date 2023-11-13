package com.trendyol.checkout.exception;

public class InvalidCartStateException extends RuntimeException {
    public InvalidCartStateException(String message) {
        super(message);
    }
}
