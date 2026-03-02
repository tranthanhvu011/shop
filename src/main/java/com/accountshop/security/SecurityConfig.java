package com.accountshop.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                    .ignoringRequestMatchers("/api/**", "/ws/**", "/webhook/payos")
            )
            .authorizeHttpRequests(auth -> auth
                // Public pages
                    .requestMatchers("/api/orders/*/status").permitAll()
                    .requestMatchers("/webhook/payos").permitAll()
                .requestMatchers("/", "/home", "/products/**", "/categories/**").permitAll()
                .requestMatchers("/auth/**", "/register", "/login", "/forgot-password", "/reset-password").permitAll()
                // Static resources
                .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**", "/webjars/**", "/favicon.ico").permitAll()
                // API endpoints (for AJAX)
                .requestMatchers("/api/cart/**").authenticated()
                .requestMatchers("/api/products/**", "/api/coupon/**").permitAll()
                .requestMatchers("/api/reviews/**", "/api/questions/**").authenticated()
                // Admin pages
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // WebSocket
                .requestMatchers("/ws/**").permitAll()
                // Everything else requires authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("taikhoan")
                .passwordParameter("matkhau")
                .successHandler((request, response, authentication) -> {
                    // Redirect based on role
                    boolean isAdmin = authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    if (isAdmin) {
                        response.sendRedirect("/admin/dashboard");
                    } else {
                        response.sendRedirect("/dashboard");
                    }
                })
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("accountshop-remember-me-key")
                .tokenValiditySeconds(604800) // 7 days
            );

        return http.build();
    }
}
