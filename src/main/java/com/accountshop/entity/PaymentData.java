package com.accountshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentData {
    private String bin;
    private String accountNumber;
    private String accountName;
    private Long amount;
    private String description;
    private Long orderCode;
    private String currency;
    private String paymentLinkId;
    private String status;
    private String checkoutUrl;
    private String qrCode;
}
