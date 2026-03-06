package com.accountshop.controller.user;

import com.accountshop.config.AppProperties;
import com.accountshop.entity.Order;
import com.accountshop.entity.PaymentMethod;
import com.accountshop.entity.User;
import com.accountshop.repository.OrderRepository;
import com.accountshop.repository.UserRepository;
import com.accountshop.security.SecurityUtils;
import com.accountshop.service.OrderService;
import com.accountshop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
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
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final AppProperties appProperties;

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
    public ResponseEntity<?> confirmPayment(@PathVariable String orderNumber) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        if (user.getBalance().compareTo(order.getTotalAmount()) >= 0) {
            user.setBalance(user.getBalance().subtract(order.getTotalAmount()));
            order.setPaymentStatus(Order.PaymentStatus.PAID);
            order.setStatus(Order.OrderStatus.COMPLETED);
            orderRepository.save(order);
            userRepository.save(user);
            return ResponseEntity.ok("Thanh toán thành công với số tiền: " + order.getTotalAmount());
        } else {
            String baseUrl = appProperties.getBaseUrl();
            PaymentMethod result = paymentService.createPaymentLink(
                    order,
                    baseUrl + "/orders/cancel",
                    baseUrl + "/orders/" + order.getOrderNumber()
            );
            return ResponseEntity.ok(result);
        }
    }
}
