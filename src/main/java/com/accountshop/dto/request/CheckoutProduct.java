package com.accountshop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CheckoutProduct {
    private Long productId;
    private Long variantProductId;
    private Long priceId;
    private int quantity;

}
