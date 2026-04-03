package com.demo.entity;

import com.demo.enums.OperatorRole;
import com.demo.enums.OrderType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLog {
    private Long id;

    private OrderType orderType;      // RECYCLE / EXCHANGE
    private Long orderId;

    private Long operatorId;
    private OperatorRole operateRole;  // USER / ADMIN / SYSTEM

    private String action;
    private String detail;

    private LocalDateTime createTime;
}
