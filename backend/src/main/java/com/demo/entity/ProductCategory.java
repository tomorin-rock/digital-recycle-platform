package com.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductCategory {
    private Long id;

    private String name;
    private Long parentId;   // 顶级为null

    private Integer status;  // 1启用 0禁用
    private Integer sort;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
