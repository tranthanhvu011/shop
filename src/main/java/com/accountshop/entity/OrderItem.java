package com.accountshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private Long productId;
    private Long variantId;
    private Long pricingId;

    private String productName;
    private String productSlug;
    private String variantName;
    private String durationLabel;
    private String productImage;

    @Column(precision = 12, scale = 0)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(precision = 12, scale = 0)
    private BigDecimal subtotal;

    @Column(columnDefinition = "TEXT")
    private String accountInfo;

    @Column(columnDefinition = "TEXT")
    private String loginGuide;

    @Column(columnDefinition = "TEXT")
    private String twoFactorGuide;

    private String sellerContactUrl;
}
