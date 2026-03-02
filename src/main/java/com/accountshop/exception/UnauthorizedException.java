package com.accountshop.exception;

/**
 * Thrown when user is not authenticated.
 */
public class UnauthorizedException extends BusinessException {

    public UnauthorizedException() {
        super("UNAUTHORIZED", "Bạn chưa đăng nhập");
    }

    public UnauthorizedException(String message) {
        super("UNAUTHORIZED", message);
    }
}
