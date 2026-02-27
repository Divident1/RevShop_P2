package com.revshop.exception;

/**
 * Thrown when a payment processing operation fails.
 */
public class PaymentFailedException extends RuntimeException {

    public PaymentFailedException(String message) {
        super(message);
    }
}
