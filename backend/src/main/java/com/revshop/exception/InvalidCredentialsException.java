package com.revshop.exception;

/**
 * Thrown when a user provides invalid credentials during authentication.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
