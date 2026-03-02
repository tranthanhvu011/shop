package com.accountshop.security;

import com.accountshop.entity.User;
import com.accountshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Utility class to get the current authenticated user
 */
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    /**
     * Get current authenticated user's email
     */
    public String getCurrentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return auth.getName();
    }

    /**
     * Get current authenticated User entity
     */
    public Optional<User> getCurrentUser() {
        String email = getCurrentEmail();
        if (email == null) return Optional.empty();
        return userRepository.findByEmail(email);
    }

    /**
     * Get current user's ID
     */
    public Long getCurrentUserId() {
        return getCurrentUser().map(User::getId).orElse(null);
    }

    /**
     * Check if current user has ADMIN role
     */
    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
