package com.accountshop.repository;

import com.accountshop.entity.VariantPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VariantPricingRepository extends JpaRepository<VariantPricing, Long> {
    List<VariantPricing> findByVariantId(Long variantId);
}
