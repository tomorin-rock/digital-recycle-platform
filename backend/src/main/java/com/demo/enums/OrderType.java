// 订单类型枚举
package com.demo.enums;

import lombok.Getter;

@Getter
public enum OrderType {
    RECYCLE("回收订单"),
    EXCHANGE("以旧换新订单");

    private final String description;

    OrderType(String description) {
        this.description = description;
    }

}
