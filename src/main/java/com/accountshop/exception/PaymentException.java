package com.accountshop.exception;

/**
 * Thrown when a payment operation fails.
 */
public class PaymentException extends BusinessException {

    public PaymentException(String message) {
        super("PAYMENT_ERROR", message);
    }
}
