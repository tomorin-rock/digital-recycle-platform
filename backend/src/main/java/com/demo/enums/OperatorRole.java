// 操作人角色枚举
package com.demo.enums;

import lombok.Getter;

@Getter
public enum OperatorRole {
    USER("用户"),
    ADMIN("管理员"),
    SYSTEM("系统");

    private final String description;

    OperatorRole(String description) {
        this.description = description;
    }

}
