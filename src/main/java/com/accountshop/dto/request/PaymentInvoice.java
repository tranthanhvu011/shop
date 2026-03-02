package com.accountshop.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentInvoice {
    private Boolean buyerNotGetInvoice;
    private Integer taxPercentage;
}
