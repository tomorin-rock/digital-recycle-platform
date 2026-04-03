package com.demo.entity;

import com.demo.enums.ProductGrade;
import com.demo.enums.RecycleOrderStatus;
import com.demo.enums.RecyclePayType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RecycleOrder {
    private Long id;

    private String orderNo;

    private Long userId;
    private Long productId;
    private Long addressId;           // 可为null（你的表允许）

    private String productSnapshot;   // 型号快照

    private ProductGrade grade;       // A/B/C/D
    private String appearanceDesc;
    private String functionDesc;

    private BigDecimal estimatePrice;
    private BigDecimal finalPrice;

    private RecycleOrderStatus status;
    private RecyclePayType payType;

    private String remark;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
