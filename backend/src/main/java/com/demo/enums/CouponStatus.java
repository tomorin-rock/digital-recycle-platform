// 优惠券状态枚举
package com.demo.enums;

import lombok.Getter;

@Getter
public enum CouponStatus {
    UNUSED("未使用"),
    USED("已使用"),
    EXPIRED("已过期");

    private final String description;

    CouponStatus(String description) {
        this.description = description;
    }

}
