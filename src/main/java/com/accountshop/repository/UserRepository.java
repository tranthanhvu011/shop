package com.accountshop.repository;

import com.accountshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Dashboard: count new users per month
    @Query("SELECT FUNCTION('YEAR', u.createdAt), FUNCTION('MONTH', u.createdAt), COUNT(u) FROM User u " +
           "WHERE u.createdAt >= :since GROUP BY FUNCTION('YEAR', u.createdAt), FUNCTION('MONTH', u.createdAt) " +
           "ORDER BY FUNCTION('YEAR', u.createdAt), FUNCTION('MONTH', u.createdAt)")
    List<Object[]> countNewUsersPerMonth(@Param("since") LocalDateTime since);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByResetToken(String resetToken);

    // Admin: filter by role
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role")
    Page<User> findByRolesContaining(@Param("role") String role, Pageable pageable);
    Optional<User> findByEmailIgnoreCaseOrUsernameIgnoreCase(String email, String username);

    // Admin: search
    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email, Pageable pageable);
}
