package com.accountshop.exception;

/**
 * Thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, Object id) {
        super("NOT_FOUND", resource + " không tìm thấy: " + id);
    }

    public ResourceNotFoundException(String message) {
        super("NOT_FOUND", message);
    }
}
