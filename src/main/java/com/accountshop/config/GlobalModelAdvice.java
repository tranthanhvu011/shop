package com.accountshop.config;

import com.accountshop.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

/**
 * Injects common model attributes into all Thymeleaf views.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final SecurityUtils securityUtils;

    @ModelAttribute
    public void addCommonAttributes(HttpServletRequest request, Model model) {
        // Current request URI for sidebar active state
        model.addAttribute("requestURI", request.getRequestURI());

        // Current user (if authenticated) — available in all templates
        if (!model.containsAttribute("currentUser")) {
            securityUtils.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));
        }
    }
}
