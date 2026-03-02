package com.accountshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_reviews")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductReview extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    private Long orderId;
    private String orderNumber;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;
}
