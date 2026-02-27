package com.revshop.exception;

/**
 * Thrown when there is insufficient stock for a product during order/cart
 * operations.
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }
}
