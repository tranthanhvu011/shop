package com.accountshop.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentRequest {
    private Integer orderCode;
    private Integer amount;
    private String description;
    private String buyerName;
    private String buyerCompanyName;
    private String buyerTaxCode;
    private String buyerAddress;
    private String buyerEmail;
    private String buyerPhone;
    private List<PaymentItems> paymentItems;
    private String cancelUrl;
    private String returnUrl;
    private PaymentInvoice paymentInvoice;
    private Long expireAt;
    private String signature;
}
