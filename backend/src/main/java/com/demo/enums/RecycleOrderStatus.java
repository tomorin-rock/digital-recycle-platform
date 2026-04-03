// 回收订单状态枚举
package com.demo.enums;

import lombok.Getter;

@Getter
public enum RecycleOrderStatus {
    SUBMITTED("已提交"),
    WAITING_INSPECTION("待质检"),
    INSPECTING("质检中"),
    WAIT_CONFIRM("待确认"),
    FINISHED("已完成"),
    CANCELLED("已取消");

    private final String description;

    RecycleOrderStatus(String description) {
        this.description = description;
    }

}
