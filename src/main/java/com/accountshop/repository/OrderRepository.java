package com.accountshop.repository;

import com.accountshop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // For user-facing order page
    @Query("SELECT o FROM Order o WHERE o.user.id = :buyerId ORDER BY o.createdAt DESC")
    Page<Order> findByBuyerIdOrderByCreatedAtDesc(@Param("buyerId") Long buyerId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.user.id = :buyerId AND CAST(o.status AS string) = :status ORDER BY o.createdAt DESC")
    Page<Order> findByBuyerIdAndStatusOrderByCreatedAtDesc(@Param("buyerId") Long buyerId, @Param("status") String status, Pageable pageable);

    long countByStatus(Order.OrderStatus status);

    // Admin: filter by status
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.paymentStatus = 'PAID'")
    BigDecimal getTotalRevenue();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'PENDING'")
    long countPending();

    long countByUserId(Long userId);
}
