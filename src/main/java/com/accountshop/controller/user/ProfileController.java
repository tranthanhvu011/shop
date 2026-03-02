package com.accountshop.controller.user;

import com.accountshop.entity.User;
import com.accountshop.security.SecurityUtils;
import com.accountshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ProfileController — handles user profile page.
 * Routes: GET/POST /profile
 */
@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final SecurityUtils securityUtils;
    private final UserService userService;

    @GetMapping("/profile")
    public String profile(Model model) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        model.addAttribute("currentUser", user);
        return "user/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam(required = false) String lastName,
                                @RequestParam(required = false) String firstName,
                                @RequestParam(required = false) String phone,
                                RedirectAttributes redirectAttributes) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Tài khoản chưa đăng nhập"));
        userService.updateProfile(user.getId(), lastName, firstName, phone);
        redirectAttributes.addFlashAttribute("success", "Cập nhập thông tin thành công");
        return "redirect:/profile";
    }
}
