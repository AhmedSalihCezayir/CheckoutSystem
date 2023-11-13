package com.trendyol.checkout.exception;

public class InvalidCartItemException extends RuntimeException {
    public InvalidCartItemException(String message) {
        super(message);
    }
}
