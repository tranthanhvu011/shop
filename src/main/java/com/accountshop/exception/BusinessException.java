package com.accountshop.exception;

import lombok.Getter;

/**
 * Base exception for all business logic errors.
 * Carries an error code for programmatic handling.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
    }
}
