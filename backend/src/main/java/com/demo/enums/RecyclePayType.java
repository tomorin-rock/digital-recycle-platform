// 回收订单支付类型枚举
package com.demo.enums;

import lombok.Getter;

@Getter
public enum RecyclePayType {
    CASH("现金"),
    COUPON("优惠券");

    private final String description;

    RecyclePayType(String description) {
        this.description = description;
    }

}
