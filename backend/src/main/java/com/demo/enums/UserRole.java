// 用户角色枚举
package com.demo.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("普通用户"),
    ADMIN("管理员");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

}
