package com.accountshop.service;

import com.accountshop.entity.*;
import com.accountshop.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * ProductService — manages products, reviews, and Q&A.
 * Ported from Product-Service microservice, simplified (no sellerId, no translation).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductReviewRepository reviewRepository;
    private final ProductQuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;

    // ===========================
    // Product listing
    // ===========================

    public Page<Product> findAll(Long categoryId, Pageable pageable) {
        if (categoryId != null) {
            return productRepository.findAllActiveByCategory(categoryId, pageable);
        }
        return productRepository.findAllActive(pageable);
    }

    public Optional<Product> findBySlug(String slug) {
        return productRepository.findBySlug(slug);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    // ===========================
    // Reviews
    // ===========================

    public Page<ProductReview> findReviews(Long productId, Pageable pageable) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable);
    }

    public Map<String, Object> getReviewStats(Long productId) {
        Map<String, Object> stats = new LinkedHashMap<>();
        long total = reviewRepository.countByProductId(productId);
        stats.put("total", total);

        Map<Integer, Long> starCounts = new LinkedHashMap<>();
        for (int star = 5; star >= 1; star--) {
            starCounts.put(star, reviewRepository.countByProductIdAndRating(productId, star));
        }
        stats.put("starCounts", starCounts);

        if (total > 0) {
            Double avg = reviewRepository.getAverageRating(productId);
            stats.put("average", avg != null ? avg : 0.0);
        } else {
            stats.put("average", 0.0);
        }

        return stats;
    }

    @Transactional
    public ProductReview createReview(Long productId, User buyer, int rating, String comment,
                                      Long orderId, String orderNumber) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        // Check if already reviewed this product in this order
        if (reviewRepository.findByProductIdAndOrderId(productId, orderId).isPresent()) {
            throw new RuntimeException("Bạn đã đánh giá sản phẩm này trong đơn hàng này");
        }

        ProductReview review = ProductReview.builder()
                .product(product)
                .buyer(buyer)
                .rating(rating)
                .comment(comment)
                .orderId(orderId)
                .orderNumber(orderNumber)
                .build();

        review = reviewRepository.save(review);

        // Update product rating
        updateProductRating(product);

        log.info("[Product] Review created for product #{} by user #{}", productId, buyer.getId());
        return review;
    }

    private void updateProductRating(Product product) {
        Double avg = reviewRepository.getAverageRating(product.getId());
        long count = reviewRepository.countByProductId(product.getId());
        product.setRatingAvg(avg != null ? avg : 0.0);
        product.setRatingCount((int) count);
        productRepository.save(product);
    }

    // ===========================
    // Q&A
    // ===========================

    public Page<ProductQuestion> getQuestions(Long productId, Pageable pageable) {
        return questionRepository.findByProductId(productId, pageable);
    }

    @Transactional
    public ProductQuestion askQuestion(Long productId, User asker, String questionContent) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        // Check duplicate
        if (questionRepository.existsByProductIdAndAskerId(productId, asker.getId())) {
            throw new RuntimeException("Bạn đã hỏi câu hỏi cho sản phẩm này");
        }

        ProductQuestion question = ProductQuestion.builder()
                .product(product)
                .asker(asker)
                .question(questionContent)
                .build();

        question = questionRepository.save(question);
        log.info("[Product] Question asked for product #{} by user #{}", productId, asker.getId());
        return question;
    }

    @Transactional
    public ProductQuestion answerQuestion(Long questionId, String answer, String adminName) {
        ProductQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Câu hỏi không tồn tại"));

        question.setAnswer(answer);
        question.setAnsweredByName(adminName);
        question.setAnsweredAt(java.time.LocalDateTime.now());
        question = questionRepository.save(question);
        log.info("[Product] Question #{} answered by admin", questionId);
        return question;
    }

    // ===========================
    // Admin
    // ===========================

    @Transactional
    public Product toggleActive(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        product.setActive(!product.getActive());
        return productRepository.save(product);
    }
}
