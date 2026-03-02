package com.accountshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(precision = 12, scale = 0)
    private BigDecimal subtotal;

    @Column(precision = 12, scale = 0)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(precision = 12, scale = 0)
    private BigDecimal totalAmount;

    private String couponCode;

    @Column(nullable = false)
    private String paymentMethod = "BANK_TRANSFER";

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String note;

    private String userEmail;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    public enum OrderStatus {
        PENDING, PROCESSING, COMPLETED, CANCELLED, REFUNDED
    }

    public enum PaymentStatus {
        PENDING, PAID, REFUNDED
    }
}
