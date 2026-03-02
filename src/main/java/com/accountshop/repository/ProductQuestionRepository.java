package com.accountshop.repository;

import com.accountshop.entity.ProductQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductQuestionRepository extends JpaRepository<ProductQuestion, Long> {
    Page<ProductQuestion> findByProductId(Long productId, Pageable pageable);
    boolean existsByProductIdAndAskerId(Long productId, Long askerId);
    long countByAnswerIsNull();
}
