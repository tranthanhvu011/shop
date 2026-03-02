package com.accountshop.repository;

import com.accountshop.entity.DigitalAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DigitalAccountRepository extends JpaRepository<DigitalAccount, Long> {
    List<DigitalAccount> findByVariantPricingIdAndStatus(Long variantPricingId, DigitalAccount.AccountStatus status);

    Optional<DigitalAccount> findFirstByVariantPricingIdAndStatus(Long variantPricingId, DigitalAccount.AccountStatus status);

    long countByVariantPricingIdAndStatus(Long variantPricingId, DigitalAccount.AccountStatus status);

    long countByStatus(DigitalAccount.AccountStatus status);

    List<DigitalAccount> findByVariantPricingId(Long variantPricingId);
}
