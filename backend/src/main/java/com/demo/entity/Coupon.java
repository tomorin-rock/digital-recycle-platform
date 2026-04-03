package com.demo.entity;

import com.demo.enums.CouponStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Coupon {
    private Long id;

    private Long userId;
    private Long sourceRecycleId;  // 来源回收订单ID

    private String code;
    private BigDecimal amount;

    private CouponStatus status;

    private LocalDateTime expireTime;
    private LocalDateTime createTime;
    private LocalDateTime useTime;
}
