package com.demo.service;

import com.demo.entity.OperationLog;
import com.demo.enums.OperatorRole;
import com.demo.enums.OrderType;
import com.github.pagehelper.PageInfo;

/**
 * 操作日志服务接口
 * 用于记录和查询系统中各类业务操作（如订单状态流转、管理员审核等）的审计日志
 */
public interface OperationLogService {
    /**
     * 通用的操作日志记录方法
     * @param log 日志实体信息
     */
    void recordOrderOperation(OperationLog log);

    /**
     * 记录回收订单相关的操作日志
     * @param orderId 订单ID
     * @param operatorId 操作人ID
     * @param role 操作人角色
     * @param action 操作动作简述
     * @param detail 操作详细描述
     */
    void recordRecycleOperation(Long orderId, Long operatorId, OperatorRole role, String action, String detail);

    /**
     * 记录换新订单相关的操作日志
     * @param orderId 订单ID
     * @param operatorId 操作人ID
     * @param role 操作人角色
     * @param action 操作动作简述
     * @param detail 操作详细描述
     */
    void recordExchangeOperation(Long orderId, Long operatorId, OperatorRole role, String action, String detail);

    /**
     * 分页查询操作日志，支持按订单类型和操作角色过滤
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @param orderType 订单类型（回收/换新）
     * @param operateRole 操作人角色
     * @return 分页后的日志列表
     */
    PageInfo<OperationLog> selectAll(Integer pageNum, Integer pageSize, OrderType orderType, OperatorRole operateRole);
}
