package com.accountshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Coupon extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountType discountType = DiscountType.PERCENTAGE;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discountValue;

    @Column(precision = 12, scale = 0)
    private BigDecimal maxDiscount;

    @Column(precision = 12, scale = 0)
    private BigDecimal minOrderAmount;

    private Integer maxUsage;
    private Integer usedCount = 0;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Boolean active = true;

    public enum DiscountType {
        PERCENTAGE, FIXED_AMOUNT
    }
}
