package com.accountshop.controller.admin;

import com.accountshop.entity.User;
import com.accountshop.repository.OrderRepository;
import com.accountshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * AdminUserController — admin user management.
 * Routes: /admin/users, /admin/api/users/{id}, /admin/users/{id}/toggle-ban, /admin/users/{id}/update-role
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @GetMapping("/users")
    public String users(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<User> users;

        if (role != null && !role.isEmpty()) {
            users = userRepository.findByRolesContaining(role, pageable);
            model.addAttribute("roleFilter", role);
        } else if (search != null && !search.isEmpty()) {
            users = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable);
            model.addAttribute("searchTerm", search);
        } else {
            users = userRepository.findAll(pageable);
        }

        model.addAttribute("users", users);
        model.addAttribute("pageTitle", "Quản lý Users");
        return "admin/users";
    }

    @GetMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<?> getUserDetail(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(u -> {
                    Map<String, Object> data = new LinkedHashMap<>();
                    data.put("id", u.getId());
                    data.put("username", u.getUsername());
                    data.put("email", u.getEmail());
                    data.put("firstName", u.getFirstName());
                    data.put("lastName", u.getLastName());
                    data.put("phone", u.getPhone());
                    data.put("avatar", u.getAvatar());
                    data.put("enabled", u.getEnabled());
                    data.put("emailVerified", u.getEmailVerified());
                    data.put("roles", u.getRoles());
                    data.put("createdAt", u.getCreatedAt());
                    data.put("lastLoginAt", u.getLastLoginAt());
                    data.put("orderCount", orderRepository.countByUserId(u.getId()));
                    return ResponseEntity.ok(data);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users/{id}/toggle-ban")
    @ResponseBody
    public ResponseEntity<?> toggleBan(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(u -> {
                    u.setEnabled(!u.getEnabled());
                    userRepository.save(u);
                    return ResponseEntity.ok(Map.of("enabled", u.getEnabled()));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users/{id}/update-role")
    @ResponseBody
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody Map<String, List<String>> body) {
        return userRepository.findById(id)
                .map(u -> {
                    List<String> roles = body.getOrDefault("roles", List.of("ROLE_USER"));
                    u.setRoles(new HashSet<>(roles));
                    userRepository.save(u);
                    return ResponseEntity.ok(Map.of("roles", u.getRoles()));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
