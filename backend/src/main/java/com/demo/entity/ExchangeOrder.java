package com.demo.entity;

import com.demo.enums.ExchangeOrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ExchangeOrder {
    private Long id;

    private String orderNo;

    private Long userId;

    private Long recycleId;     // 对应回收订单
    private Long couponId;      // 可为null
    private Long newProductId;  // 新机商品ID
    private Long addressId;

    private BigDecimal productPrice;
    private BigDecimal discountAmount;
    private BigDecimal payAmount;

    private ExchangeOrderStatus status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
