package com.demo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserAddress {
    private Long id;

    private Long userId;

    private String receiverName;
    private String receiverPhone;

    private String province;
    private String city;
    private String district;
    private String detail;

    private Integer isDefault; // 1是 0否

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
