package com.revshop.exception;

/**
 * Thrown when a duplicate resource is detected (e.g. duplicate email, duplicate
 * review).
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
