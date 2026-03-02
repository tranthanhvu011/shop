package com.accountshop.controller;

import com.accountshop.entity.Coupon;
import com.accountshop.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * CouponApiController — REST API for coupon validation on checkout page.
 */
@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponApiController {

    private final CouponRepository couponRepository;

    @PostMapping("/validate")
    public ResponseEntity<?> validateCoupon(@RequestBody Map<String, Object> body) {
        String code = (String) body.getOrDefault("code", "");
        BigDecimal subtotal = new BigDecimal(body.getOrDefault("subtotal", "0").toString());

        if (code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", "Vui lòng nhập mã giảm giá"));
        }

        Coupon coupon = couponRepository.findByCode(code.trim().toUpperCase()).orElse(null);
        if (coupon == null || !coupon.getActive()) {
            return ResponseEntity.ok(Map.of("valid", false, "message", "Mã giảm giá không hợp lệ"));
        }

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            return ResponseEntity.ok(Map.of("valid", false, "message", "Mã giảm giá chưa có hiệu lực"));
        }
        if (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate())) {
            return ResponseEntity.ok(Map.of("valid", false, "message", "Mã giảm giá đã hết hạn"));
        }
        if (coupon.getMaxUsage() != null && coupon.getUsedCount() >= coupon.getMaxUsage()) {
            return ResponseEntity.ok(Map.of("valid", false, "message", "Mã giảm giá đã hết lượt sử dụng"));
        }
        if (coupon.getMinOrderAmount() != null && subtotal.compareTo(coupon.getMinOrderAmount()) < 0) {
            return ResponseEntity.ok(Map.of("valid", false, "message",
                    "Đơn hàng tối thiểu " + coupon.getMinOrderAmount().toBigInteger() + "₫ để sử dụng mã này"));
        }

        BigDecimal discount;
        if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
            discount = subtotal.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.FLOOR);
            if (coupon.getMaxDiscount() != null && discount.compareTo(coupon.getMaxDiscount()) > 0) {
                discount = coupon.getMaxDiscount();
            }
        } else {
            discount = coupon.getDiscountValue();
        }
        if (discount.compareTo(subtotal) > 0) discount = subtotal;

        return ResponseEntity.ok(Map.of(
                "valid", true,
                "discountAmount", discount,
                "message", "Áp dụng thành công! Giảm " + discount.toBigInteger() + "₫"
        ));
    }
}
