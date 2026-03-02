package com.accountshop.controller.admin;

import com.accountshop.entity.Order;
import com.accountshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * AdminOrderController — admin order management.
 * Routes: GET /admin/orders
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderRepository orderRepository;

    @GetMapping("/orders")
    public String orders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<Order> orders;

        if (status != null && !status.isEmpty()) {
            orders = orderRepository.findByStatus(Order.OrderStatus.valueOf(status), pageable);
            model.addAttribute("statusFilter", status);
        } else {
            orders = orderRepository.findAll(pageable);
        }

        model.addAttribute("orders", orders);
        model.addAttribute("pendingCount", orderRepository.countByStatus(Order.OrderStatus.PENDING));
        model.addAttribute("completedCount", orderRepository.countByStatus(Order.OrderStatus.COMPLETED));
        model.addAttribute("pageTitle", "Quản lý Orders");
        return "admin/orders";
    }
}
