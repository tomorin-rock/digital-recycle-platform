package com.demo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecycleDTO {
    private Long productId;
    private Long addressId;
    private String grade;
    private String appearanceDesc;
    private String functionDesc;
    private BigDecimal estimatePrice;
}
