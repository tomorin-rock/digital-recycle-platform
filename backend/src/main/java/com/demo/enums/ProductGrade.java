// 商品成色等级枚举
package com.demo.enums;

import lombok.Getter;

@Getter
public enum ProductGrade {
    A("A级 - 几乎全新"),
    B("B级 - 轻微使用痕迹"),
    C("C级 - 明显使用痕迹"),
    D("D级 - 严重磨损");

    private final String description;

    ProductGrade(String description) {
        this.description = description;
    }

}
