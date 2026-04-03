package com.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductBrand {
    private Long id;

    private String name;
    private String logo;

    private Integer status; // 1启用 0禁用
    private Integer sort;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
