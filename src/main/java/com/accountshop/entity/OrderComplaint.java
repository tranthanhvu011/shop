package com.accountshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_complaints")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderComplaint extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String adminResponse;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ComplaintStatus status = ComplaintStatus.OPEN;

    private LocalDateTime resolvedAt;

    public enum ComplaintStatus {
        OPEN, RESPONDED, RESOLVED, CLOSED
    }
}
