package com.demo.controller.admin;

import com.demo.common.Result;
import com.demo.entity.OperationLog;
import com.demo.enums.OperatorRole;
import com.demo.enums.OrderType;
import com.demo.service.OperationLogService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员-订单操作日志管理控制器
 */
@RestController
@RequestMapping("api/admin/orderLogs")
public class AdminOrderLogController {

    private final OperationLogService operationLogService;

    public AdminOrderLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    /**
     * 条件分页查询系统操作日志
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param orderType 订单类型（回收/换新）
     * @param operateRole 操作人角色
     * @return 日志列表分页数据
     */
    @GetMapping
    public Result selectAll(@RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize,
                            @RequestParam(required = false) OrderType orderType,
                            @RequestParam(required = false) OperatorRole operateRole) {
        PageInfo<OperationLog> logs = operationLogService.selectAll(pageNum, pageSize, orderType, operateRole);
        return Result.success(logs);
    }
}
