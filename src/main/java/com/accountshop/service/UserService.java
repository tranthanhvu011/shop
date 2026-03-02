package com.accountshop.service;

import com.accountshop.common.RedisConstants;
import com.accountshop.config.JwtTokenProvider;
import com.accountshop.dto.request.LoginRequest;
import com.accountshop.dto.response.AuthResponse;
import com.accountshop.entity.User;
import com.accountshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public User register(String username, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email đã được sử dụng");
        }
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username đã được sử dụng");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .emailVerified(true)
                .roles(new HashSet<>(Set.of("USER")))
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public AuthResponse loginUser(LoginRequest loginRequest) {
        String key =  loginRequest.getTaikhoan().trim();
        User user = userRepository.findByEmailIgnoreCaseOrUsernameIgnoreCase(key, key).orElseThrow(() -> new BadCredentialsException("Sai tài khoản hoặc mật khẩu"));
        if (!passwordEncoder.matches(loginRequest.getMatkhau(), user.getPassword())) {
            throw new BadCredentialsException("Sai tài khoản hoặc mật khẩu");
        }
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername(), user.getRoles());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getUsername());
        redisTemplate.opsForValue().set(RedisConstants.REFRESH_TOKEN_PREFIX + user.getEmail(), refreshToken, RedisConstants.REFRESH_EXPIRE_DAYS, TimeUnit.DAYS);
        user.setLastLoginAt(LocalDateTime.now());
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(String.valueOf(user.getId()))
                .email(user.getEmail())
                .roles(user.getRoles())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .avatar(user.getAvatar())
                .build();

    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional
    public User updateProfile(Long userId, String firstName, String lastName, String phone) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (phone != null) user.setPhone(phone);
        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public User updateAvatar(Long userId, String avatarPath) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        user.setAvatar(avatarPath);
        return userRepository.save(user);
    }

    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        user.setEnabled(!user.getEnabled());
        userRepository.save(user);
    }

    @Transactional
    public String createResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElse(null);
        if (user == null) {
            return null; // Don't reveal if email exists
        }
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);
        return token;
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token không hợp lệ hoặc đã hết hạn"));
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token đã hết hạn. Vui lòng yêu cầu đặt lại mật khẩu mới.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    public boolean isValidResetToken(String token) {
        return userRepository.findByResetToken(token)
                .filter(u -> u.getResetTokenExpiry() != null && u.getResetTokenExpiry().isAfter(LocalDateTime.now()))
                .isPresent();
    }

}
