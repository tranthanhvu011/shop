package com.accountshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "variant_pricing")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VariantPricing extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    private Integer durationMonths;

    private String durationLabel;

    @Column(nullable = false, precision = 12, scale = 0)
    private BigDecimal price;

    @Column(precision = 12, scale = 0)
    private BigDecimal originalPrice;

    @Column(columnDefinition = "TEXT")
    private String warrantyPolicy;

    @Column(columnDefinition = "TEXT")
    private String accountDescription;

    @Column(nullable = false)
    private Integer availableStock = 0;
}
