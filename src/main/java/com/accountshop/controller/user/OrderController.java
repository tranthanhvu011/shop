package com.accountshop.controller.user;

import com.accountshop.entity.Order;
import com.accountshop.entity.User;
import com.accountshop.repository.OrderRepository;
import com.accountshop.security.SecurityUtils;
import com.accountshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * OrderController — handles user order pages.
 * Routes: /orders, /orders/{orderNumber}, /orders/{orderNumber}/cancel, /orders/{orderNumber}/confirm-payment
 */
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final SecurityUtils securityUtils;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @GetMapping("/orders")
    public String orders(@RequestParam(required = false) String status,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        model.addAttribute("currentUser", user);

        var pageable = PageRequest.of(page, 10);
        if (status != null && !status.isEmpty()) {
            model.addAttribute("orders", orderRepository.findByBuyerIdAndStatusOrderByCreatedAtDesc(user.getId(), status, pageable));
            model.addAttribute("selectedStatus", status);
        } else {
            model.addAttribute("orders", orderRepository.findByBuyerIdOrderByCreatedAtDesc(user.getId(), pageable));
        }
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", 1);
        return "user/orders";
    }

    @GetMapping("/orders/{orderNumber}")
    public String orderDetail(@PathVariable String orderNumber, Model model) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        model.addAttribute("currentUser", user);

        Order order = orderService.getOrderDetail(user, orderNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        model.addAttribute("order", order);
        return "user/order-detail";
    }

    @PostMapping("/orders/{orderNumber}/cancel")
    public String cancelOrder(@PathVariable String orderNumber, RedirectAttributes redirectAttributes) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        orderService.cancelOrder(user, orderNumber);
        redirectAttributes.addFlashAttribute("success", "Đã hủy đơn hàng");
        return "redirect:/orders/" + orderNumber;
    }

    @PostMapping("/orders/{orderNumber}/confirm-payment")
    public String confirmPayment(@PathVariable String orderNumber, RedirectAttributes redirectAttributes) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        try {
            orderService.confirmPayment(user, orderNumber);
            redirectAttributes.addFlashAttribute("success", "Thanh toán đã được xác nhận! Tài khoản đã được cấp phát.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders/" + orderNumber;
    }
}
