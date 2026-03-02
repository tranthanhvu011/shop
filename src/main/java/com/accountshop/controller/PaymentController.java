package com.accountshop.controller;

import com.accountshop.dto.request.PaymentRequest;
import com.accountshop.entity.PaymentMethod;
import com.accountshop.entity.User;
import com.accountshop.security.SecurityUtils;
import com.accountshop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final SecurityUtils securityUtils;

    @PostMapping("/createLinkPayment")
    public ResponseEntity<PaymentMethod> createLinkPayment(@RequestBody PaymentRequest  paymentRequest) {
        User user = securityUtils.getCurrentUser().orElseThrow(() -> new RuntimeException(("user null")));
        PaymentMethod paymentMethod = paymentService.createPaymentLink(user.getId(), paymentRequest);
        return ResponseEntity.ok().body(paymentMethod);
    }
}
