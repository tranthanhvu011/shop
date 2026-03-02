package com.accountshop.service;

import com.accountshop.entity.*;
import com.accountshop.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OrderService — handles order creation, payment, complaints.
 * Ported from Order-Service microservice. Simplified: admin = sole owner.
 * Uses EmailService (Kafka producer) for async email delivery.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CouponRepository couponRepository;
    private final OrderComplaintRepository complaintRepository;
    private final DigitalAccountRepository digitalAccountRepository;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    // ===========================
    // Create Order
    // ===========================
    @Transactional
    public Order createOrder(User user, String paymentMethod, String couponCode, String note,
                             List<Map<String, Object>> items) {
        // 1. Calculate subtotal
        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (Map<String, Object> itemData : items) {
            BigDecimal unitPrice = new BigDecimal(itemData.get("unitPrice").toString());
            int quantity = Integer.parseInt(itemData.get("quantity").toString());
            BigDecimal itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            subtotal = subtotal.add(itemSubtotal);

            OrderItem orderItem = OrderItem.builder()
                    .productId(Long.parseLong(itemData.get("productId").toString()))
                    .variantId(itemData.get("variantId") != null ? Long.parseLong(itemData.get("variantId").toString()) : null)
                    .pricingId(itemData.get("pricingId") != null ? Long.parseLong(itemData.get("pricingId").toString()) : null)
                    .productName(itemData.get("productName") != null ? itemData.get("productName").toString() : "Sản phẩm")
                    .productSlug(itemData.get("productSlug") != null ? itemData.get("productSlug").toString() : "")
                    .variantName(itemData.get("variantName") != null ? itemData.get("variantName").toString() : "")
                    .durationLabel(itemData.get("durationLabel") != null ? itemData.get("durationLabel").toString() : "")
                    .productImage(itemData.get("productImage") != null ? itemData.get("productImage").toString() : "")
                    .unitPrice(unitPrice)
                    .quantity(quantity)
                    .subtotal(itemSubtotal)
                    .build();
            orderItems.add(orderItem);
        }

        // 2. Apply coupon
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (couponCode != null && !couponCode.isBlank()) {
            discountAmount = calculateDiscount(couponCode, subtotal);
        }

        // 3. Total
        BigDecimal totalAmount = subtotal.subtract(discountAmount).max(BigDecimal.ZERO);

        // 4. Create order
        Order order = Order.builder()
                .user(user)
                .orderNumber(generateOrderNumber())
                .status(Order.OrderStatus.PENDING)
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .couponCode(couponCode)
                .paymentMethod(paymentMethod != null ? paymentMethod : "BANK_TRANSFER")
                .paymentStatus(Order.PaymentStatus.PENDING)
                .note(note)
                .userEmail(user.getEmail())
                .build();

        // 5. Link items
        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        order.setItems(orderItems);

        Order saved = orderRepository.save(order);
        log.info("[Order] Created order #{} for userId={}, total={}", saved.getOrderNumber(), user.getId(), totalAmount);
        return saved;
    }

    // ===========================
    // Cancel Order
    // ===========================

    @Transactional
    public Order cancelOrder(User user, String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .filter(o -> o.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng đang chờ thanh toán");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    // ===========================
    // Confirm Payment
    // ===========================

    @Transactional
    public Order confirmPayment(User user, String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .filter(o -> o.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Đơn hàng không ở trạng thái chờ thanh toán");
        }

        order.setPaymentStatus(Order.PaymentStatus.PAID);

        // Allocate digital accounts for each item
        StringBuilder allAccountsHtml = new StringBuilder();
        for (OrderItem item : order.getItems()) {
            if (item.getPricingId() != null) {
                String accountJson = allocateDigitalAccounts(order.getId(), item.getPricingId(), item.getQuantity());
                if (accountJson != null) {
                    item.setAccountInfo(accountJson);
                    allAccountsHtml.append(buildItemAccountHtml(item.getProductName(), item.getVariantName(),
                            item.getDurationLabel(), accountJson));
                }
            }
        }

        // Mark as completed
        order.setStatus(Order.OrderStatus.COMPLETED);
        Order saved = orderRepository.save(order);

        // Send email via Kafka
        if (order.getUserEmail() != null && allAccountsHtml.length() > 0) {
            String html = buildOrderEmailHtml(order, allAccountsHtml.toString());
            emailService.sendOrderEmail(order.getUserEmail(),
                    "Thông tin tài khoản đơn hàng #" + order.getOrderNumber(), html);
        }

        log.info("[Order] Payment confirmed and completed for order #{}", orderNumber);
        return saved;
    }

    // ===========================
    // Admin Order Management
    // ===========================

    @Transactional
    public Order adminConfirmPayment(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Đơn hàng không ở trạng thái chờ thanh toán");
        }

        order.setPaymentStatus(Order.PaymentStatus.PAID);

        // Allocate digital accounts
        StringBuilder allAccountsHtml = new StringBuilder();
        for (OrderItem item : order.getItems()) {
            if (item.getPricingId() != null) {
                String accountJson = allocateDigitalAccounts(order.getId(), item.getPricingId(), item.getQuantity());
                if (accountJson != null) {
                    item.setAccountInfo(accountJson);
                    allAccountsHtml.append(buildItemAccountHtml(item.getProductName(), item.getVariantName(),
                            item.getDurationLabel(), accountJson));
                }
            }
        }

        order.setStatus(Order.OrderStatus.COMPLETED);
        Order saved = orderRepository.save(order);

        // Send email via Kafka
        if (order.getUserEmail() != null && allAccountsHtml.length() > 0) {
            String html = buildOrderEmailHtml(order, allAccountsHtml.toString());
            emailService.sendOrderEmail(order.getUserEmail(),
                    "Thông tin tài khoản đơn hàng #" + order.getOrderNumber(), html);
        }

        log.info("[Order] Admin confirmed payment for order #{}", orderNumber);
        return saved;
    }

    // ===========================
    // Digital Account Allocation
    // ===========================

    private String allocateDigitalAccounts(Long orderId, Long pricingId, int quantity) {
        List<DigitalAccount> available = digitalAccountRepository
                .findByVariantPricingIdAndStatus(pricingId, DigitalAccount.AccountStatus.AVAILABLE);

        if (available.isEmpty()) return null;

        int allocateCount = Math.min(quantity, available.size());
        List<Map<String, String>> allocated = new ArrayList<>();

        for (int i = 0; i < allocateCount; i++) {
            DigitalAccount account = available.get(i);
            account.setStatus(DigitalAccount.AccountStatus.SOLD);
            account.setOrderId(orderId);
            digitalAccountRepository.save(account);

            Map<String, String> info = new LinkedHashMap<>();
            info.put("accountInfo", account.getAccountInfo());
            allocated.add(info);
        }

        try {
            return objectMapper.writeValueAsString(allocated);
        } catch (Exception e) {
            log.error("Failed to serialize account info: {}", e.getMessage());
            return null;
        }
    }

    // ===========================
    // Get Orders
    // ===========================

    public Page<Order> getOrders(Long userId, int page, int size) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
    }

    public Optional<Order> getOrderDetail(User user, String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .filter(o -> o.getUser().getId().equals(user.getId()));
    }

    public Optional<Order> getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    public Page<Order> getAllOrders(int page, int size) {
        return orderRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

    // ===========================
    // Complaint System
    // ===========================

    @Transactional
    public OrderComplaint createComplaint(User user, String orderNumber, String reason) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .filter(o -> o.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        if (order.getStatus() != Order.OrderStatus.COMPLETED) {
            throw new RuntimeException("Chỉ có thể khiếu nại đơn hàng đã hoàn tất");
        }

        OrderComplaint complaint = OrderComplaint.builder()
                .order(order)
                .user(user)
                .reason(reason)
                .status(OrderComplaint.ComplaintStatus.OPEN)
                .build();

        return complaintRepository.save(complaint);
    }

    @Transactional
    public OrderComplaint respondToComplaint(Long complaintId, String response) {
        OrderComplaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Khiếu nại không tồn tại"));

        complaint.setAdminResponse(response);
        complaint.setStatus(OrderComplaint.ComplaintStatus.RESPONDED);
        return complaintRepository.save(complaint);
    }

    @Transactional
    public OrderComplaint resolveComplaint(User user, String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .filter(o -> o.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        OrderComplaint complaint = complaintRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new RuntimeException("Khiếu nại không tồn tại"));

        complaint.setStatus(OrderComplaint.ComplaintStatus.RESOLVED);
        complaint.setResolvedAt(LocalDateTime.now());
        return complaintRepository.save(complaint);
    }

    // ===========================
    // Coupon
    // ===========================

    private BigDecimal calculateDiscount(String code, BigDecimal subtotal) {
        Coupon coupon = couponRepository.findByCode(code.trim().toUpperCase()).orElse(null);
        if (coupon == null || !coupon.getActive()) return BigDecimal.ZERO;

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) return BigDecimal.ZERO;
        if (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate())) return BigDecimal.ZERO;
        if (coupon.getMaxUsage() != null && coupon.getUsedCount() >= coupon.getMaxUsage()) return BigDecimal.ZERO;
        if (coupon.getMinOrderAmount() != null && subtotal.compareTo(coupon.getMinOrderAmount()) < 0) return BigDecimal.ZERO;

        BigDecimal discount;
        if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
            discount = subtotal.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.FLOOR);
            if (coupon.getMaxDiscount() != null && discount.compareTo(coupon.getMaxDiscount()) > 0) {
                discount = coupon.getMaxDiscount();
            }
        } else {
            discount = coupon.getDiscountValue();
        }

        if (discount.compareTo(subtotal) > 0) discount = subtotal;

        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);

        return discount;
    }

    // ===========================
    // Helpers
    // ===========================

    private String generateOrderNumber() {
        String datePart = java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "ORD-" + datePart + "-" + randomPart;
    }

    private String buildOrderEmailHtml(Order order, String accountsHtml) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; background: #f5f5f5;">
                <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; border-radius: 10px; text-align: center;">
                    <h1 style="color: white; margin: 0;">🎉 Thanh toán thành công!</h1>
                </div>
                <div style="background: #f8f9fa; padding: 20px; border-radius: 10px; margin-top: 20px;">
                    <h2 style="color: #333;">Đơn hàng #%s</h2>
                    <p><strong>Tổng tiền:</strong> %s đ</p>
                </div>
                <div style="margin-top: 20px;">
                    <h2 style="color: #333;">📋 Thông tin tài khoản</h2>
                    %s
                </div>
                <div style="margin-top: 30px; padding: 15px; background: #fff3cd; border-radius: 10px;">
                    <p style="margin: 0; color: #856404;">⚠️ <strong>Lưu ý:</strong> Vui lòng lưu lại thông tin tài khoản.</p>
                </div>
                <p style="color: #888; font-size: 12px; margin-top: 20px; text-align: center;">AccountShop — Cảm ơn bạn đã mua hàng!</p>
            </body>
            </html>
            """.formatted(order.getOrderNumber(), order.getTotalAmount().toPlainString(), accountsHtml);
    }

    @SuppressWarnings("unchecked")
    private String buildItemAccountHtml(String productName, String variantName,
                                         String durationLabel, String accountJson) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='background: white; border: 1px solid #ddd; border-radius: 8px; padding: 15px; margin: 10px 0;'>");
        sb.append("<h3 style='margin: 0 0 10px; color: #333;'>🛒 ").append(productName);
        if (variantName != null && !variantName.isEmpty()) sb.append(" — ").append(variantName);
        if (durationLabel != null && !durationLabel.isEmpty()) sb.append(" (").append(durationLabel).append(")");
        sb.append("</h3>");

        try {
            List<Map<String, String>> accounts = objectMapper.readValue(accountJson, List.class);
            for (int i = 0; i < accounts.size(); i++) {
                Map<String, String> acc = accounts.get(i);
                sb.append("<div style='background: #f0f7ff; padding: 10px; border-radius: 5px; margin: 5px 0;'>");
                sb.append("<p style='margin: 2px 0;'><strong>Tài khoản ").append(i + 1).append(":</strong></p>");
                sb.append("<p style='margin: 2px 0;'>").append(acc.getOrDefault("accountInfo", "N/A")).append("</p>");
                sb.append("</div>");
            }
        } catch (Exception e) {
            sb.append("<p>Dữ liệu tài khoản không khả dụng</p>");
        }

        sb.append("</div>");
        return sb.toString();
    }
}
