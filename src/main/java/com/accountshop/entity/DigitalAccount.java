package com.accountshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "digital_accounts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DigitalAccount extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_pricing_id", nullable = false)
    private VariantPricing variantPricing;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String accountInfo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.AVAILABLE;

    private Long orderId;
    private Long allocatedToUserId;

    public enum AccountStatus {
        AVAILABLE, ALLOCATED, SOLD
    }
}
