// 以旧换新订单状态枚举
package com.demo.enums;

import lombok.Getter;

@Getter
public enum ExchangeOrderStatus {
    UNPAID("待支付"),
    PAID("已支付"),
    SHIPPED("已发货"),
    FINISHED("已完成"),
    CANCELLED("已取消");

    private final String description;

    ExchangeOrderStatus(String description) {
        this.description = description;
    }

}
