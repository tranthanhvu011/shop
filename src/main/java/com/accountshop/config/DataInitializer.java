package com.accountshop.config;

import com.accountshop.entity.User;
import com.accountshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Creates default admin account on first startup
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create default admin if not exists
        if (!userRepository.existsByEmail("admin@accountshop.com")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@accountshop.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Admin")
                    .lastName("Shop")
                    .enabled(true)
                    .emailVerified(true)
                    .roles(new HashSet<>(Set.of("ADMIN", "USER")))
                    .build();
            userRepository.save(admin);
            log.info("✅ Default admin account created: admin@accountshop.com / admin123");
        }
    }
}
