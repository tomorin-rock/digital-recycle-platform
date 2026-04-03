package com.demo.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;

    private Long categoryId;
    private Long brandId;

    private String modelName;     // 如 iPhone 15 128G
    private String description;
    private String imgUrl;

    private Integer isNew;        // 1 新机 0 仅回收型号

    private BigDecimal marketPrice;       // 新机售价
    private BigDecimal recycleBasePrice;  // 回收基准价

    private Integer stock;        // 新机库存
    private Integer status;       // 1上架 0下架

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

