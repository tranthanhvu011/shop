package com.accountshop.exception;

import com.accountshop.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * GlobalExceptionHandler — centralized exception handling for the application.
 * - REST API endpoints → returns ApiResponse JSON
 * - Thymeleaf pages → redirects with flash error message
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle ResourceNotFoundException → 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("[404] {} — {}", request.getRequestURI(), ex.getMessage());

        if (isApiRequest(request)) {
            ApiResponse<?> response = ApiResponse.error(ex.getErrorCode(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        // For page requests, return error page or redirect
        return "error/404";
    }

    /**
     * Handle UnauthorizedException → 401 or redirect to login
     */
    @ExceptionHandler(UnauthorizedException.class)
    public Object handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        log.warn("[401] {} — {}", request.getRequestURI(), ex.getMessage());

        if (isApiRequest(request)) {
            ApiResponse<?> response = ApiResponse.error(ex.getErrorCode(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return "redirect:/login";
    }

    /**
     * Handle PaymentException → 400
     */
    @ExceptionHandler(PaymentException.class)
    public Object handlePayment(PaymentException ex, HttpServletRequest request) {
        log.error("[Payment Error] {} — {}", request.getRequestURI(), ex.getMessage());

        if (isApiRequest(request)) {
            ApiResponse<?> response = ApiResponse.error(ex.getErrorCode(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return "redirect:/checkout";
    }

    /**
     * Handle generic BusinessException → 400
     */
    @ExceptionHandler(BusinessException.class)
    public Object handleBusiness(BusinessException ex, HttpServletRequest request) {
        log.warn("[Business Error] {} — {}", request.getRequestURI(), ex.getMessage());

        if (isApiRequest(request)) {
            ApiResponse<?> response = ApiResponse.error(ex.getErrorCode(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return "error/400";
    }

    /**
     * Catch-all for unexpected exceptions → 500
     */
    @ExceptionHandler(Exception.class)
    public Object handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("[500] {} — {}", request.getRequestURI(), ex.getMessage(), ex);

        if (isApiRequest(request)) {
            ApiResponse<?> response = ApiResponse.error("INTERNAL_ERROR", "Đã xảy ra lỗi hệ thống");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        return "error/500";
    }

    // ── Helper ──

    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        return uri.startsWith("/api/")
                || uri.startsWith("/webhook/")
                || (accept != null && accept.contains("application/json"))
                || "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
}
