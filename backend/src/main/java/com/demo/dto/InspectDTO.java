package com.demo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InspectDTO {
    private BigDecimal finalPrice; // 最终回收价格，必填
    private String remark; //质检后备注
}
