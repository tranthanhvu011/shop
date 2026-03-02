package com.accountshop.controller.admin;

import com.accountshop.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * AdminSettingsController — admin settings and webhook config.
 * Routes: GET /admin/settings, POST /admin/payos/confirm-webhook
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminSettingsController {

    private final AdminService adminService;

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("pageTitle", "Cài đặt");
        return "admin/settings";
    }

    @PostMapping("/payos/confirm-webhook")
    @ResponseBody
    public ResponseEntity<?> confirmWebhook() {
        return ResponseEntity.ok(adminService.confirmFromConfig());
    }
}
