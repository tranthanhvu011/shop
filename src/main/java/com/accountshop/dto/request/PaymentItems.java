package com.accountshop.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentItems {
    private String name;
    private Integer quantity;
    private Double price;
    private String unit;
    private Integer taxPercentage;

}
