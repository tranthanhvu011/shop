package com.accountshop.repository;

import com.accountshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySlug(String slug);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.isDeleted = false")
    Page<Product> findAllActive(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.isDeleted = false AND p.category.id = :categoryId")
    Page<Product> findAllActiveByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.active = true AND p.isDeleted = false")
    long countActive();

    // Admin: filter by active status
    Page<Product> findByActiveTrue(Pageable pageable);
    Page<Product> findByActiveFalse(Pageable pageable);

    // Admin: search
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
