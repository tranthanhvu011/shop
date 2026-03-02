package com.accountshop.controller.admin;

import com.accountshop.entity.Order;
import com.accountshop.repository.OrderRepository;
import com.accountshop.repository.ProductRepository;
import com.accountshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * AdminDashboardController — admin dashboard with stats.
 * Routes: GET /admin/dashboard
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalProducts", productRepository.count());
        model.addAttribute("totalOrders", orderRepository.count());
        model.addAttribute("pendingOrders", orderRepository.countByStatus(Order.OrderStatus.PENDING));
        model.addAttribute("completedOrders", orderRepository.countByStatus(Order.OrderStatus.COMPLETED));
        model.addAttribute("totalRevenue", orderRepository.getTotalRevenue());
        return "admin/dashboard";
    }
}
