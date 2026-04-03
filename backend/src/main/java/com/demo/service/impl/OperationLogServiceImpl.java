package com.demo.service.impl;

import com.demo.entity.OperationLog;
import com.demo.enums.OperatorRole;
import com.demo.enums.OrderType;
import com.demo.mapper.OrderOperationMapper;
import com.demo.service.OperationLogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.demo.enums.OrderType.*;

/**
 * 操作日志服务实现类
 */
@Service
@Transactional(readOnly = true)
public class OperationLogServiceImpl implements OperationLogService {

    private final OrderOperationMapper orderOperationMapper;

    public OperationLogServiceImpl(OrderOperationMapper orderOperationMapper) {
        this.orderOperationMapper = orderOperationMapper;
    }

    @Override
    @Transactional
    public void recordOrderOperation(OperationLog log) {
        orderOperationMapper.insert(log);
    }

    @Override
    @Transactional
    public void recordRecycleOperation(Long orderId, Long operatorId, OperatorRole role, String action, String detail) {
        recordOrder(orderId, operatorId, role, action, detail, RECYCLE);
    }

    @Override
    @Transactional
    public void recordExchangeOperation(Long orderId, Long operatorId, OperatorRole role, String action, String detail) {
        recordOrder(orderId, operatorId, role, action, detail, EXCHANGE);
    }

    @Override
    public PageInfo<OperationLog> selectAll(Integer pageNum, Integer pageSize, OrderType orderType, OperatorRole operateRole) {
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(orderOperationMapper.selectAll(orderType, operateRole));
    }

    /**
     * 内部方法：构造并保存操作日志
     */
    private void recordOrder(Long orderId, Long operatorId, OperatorRole role, String action, String detail, OrderType orderType) {
        OperationLog log = new OperationLog();
        log.setOrderType(orderType);
        log.setOrderId(orderId);
        log.setOperatorId(operatorId);
        log.setOperateRole(role);
        log.setAction(action);
        log.setDetail(detail);
        log.setCreateTime(LocalDateTime.now());
        recordOrderOperation(log);
    }
}
