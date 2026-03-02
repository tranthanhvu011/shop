package com.accountshop.repository;

import com.accountshop.entity.OrderComplaint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderComplaintRepository extends JpaRepository<OrderComplaint, Long> {
    Optional<OrderComplaint> findByOrderId(Long orderId);
    Page<OrderComplaint> findAllByOrderByCreatedAtDesc(Pageable pageable);
    long countByStatus(OrderComplaint.ComplaintStatus status);
}
