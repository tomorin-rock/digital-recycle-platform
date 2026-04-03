package com.demo.service.impl;

import com.demo.entity.ExchangeOrder;
import com.demo.entity.SysUser;
import com.demo.enums.OperatorRole;
import com.demo.mapper.CouponMapper;
import com.demo.mapper.ExchangeMapper;
import com.demo.mapper.UserMapper;
import com.demo.service.ExchangeService;
import com.demo.service.OperationLogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.demo.enums.ExchangeOrderStatus.*;

/**
 * 以旧换新订单服务实现类
 */
@Service
@Transactional(readOnly = true)
public class ExchangeServiceImpl implements ExchangeService {

    private final ExchangeMapper exchangeMapper;
    private final CouponMapper couponMapper;
    private final UserMapper userMapper;
    private final OperationLogService operationLogService;

    public ExchangeServiceImpl(ExchangeMapper exchangeMapper, 
                               CouponMapper couponMapper, 
                               UserMapper userMapper, 
                               OperationLogService operationLogService) {
        this.exchangeMapper = exchangeMapper;
        this.couponMapper = couponMapper;
        this.userMapper = userMapper;
        this.operationLogService = operationLogService;
    }

    @Override
    @Transactional
    public int insert(ExchangeOrder exchangeOrder) {
        int result = exchangeMapper.insert(exchangeOrder);
        if (result > 0) {
            // 记录创建订单日志
            operationLogService.recordExchangeOperation(
                    exchangeOrder.getId(),
                    exchangeOrder.getUserId(),
                    OperatorRole.USER,
                    "创建订单",
                    String.format("创建换新订单，应付金额：%s元", exchangeOrder.getPayAmount())
            );
        }
        return result;
    }

    @Override
    public PageInfo<ExchangeOrder> selectAll(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ExchangeOrder> orderList = exchangeMapper.selectAll();
        return new PageInfo<>(orderList);
    }

    @Override
    public List<ExchangeOrder> selectByUserId(Long userId) {
        return exchangeMapper.selectByUserId(userId);
    }

    @Override
    public ExchangeOrder selectById(Long id) {
        return exchangeMapper.selectById(id);
    }

    @Override
    @Transactional
    public int payOrder(String orderNo, Long userId) {
        // 1. 校验订单
        ExchangeOrder order = exchangeMapper.selectByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("订单不存在或不属于当前用户");
        }
        if (order.getStatus() != UNPAID) {
            throw new IllegalArgumentException("订单状态不允许支付");
        }

        // 2. 校验余额
        SysUser user = userMapper.selectById(userId);
        if (user.getBalance().compareTo(order.getPayAmount()) < 0) {
            return -2; // 余额不足
        }

        // 3. 扣除余额
        userMapper.reduceBalance(userId, order.getPayAmount());

        // 4. 更新订单状态
        int result = exchangeMapper.updateStatus(orderNo, PAID);

        // 5. 若使用了优惠券，核销优惠券
        if (order.getCouponId() != null) {
            couponMapper.useCoupon(order.getCouponId(), order.getRecycleId());
        }

        // 6. 记录支付日志
        operationLogService.recordExchangeOperation(
                order.getId(),
                userId,
                OperatorRole.USER,
                "支付订单",
                String.format("支付订单，支付金额：%s元", order.getPayAmount())
        );
        return result;
    }

    @Override
    @Transactional
    public void shipping(String orderNo, Long userId) {
        ExchangeOrder order = exchangeMapper.selectByOrderNo(orderNo);
        if (order.getStatus() != PAID) {
            throw new IllegalArgumentException("订单状态不允许发货");
        }
        exchangeMapper.updateStatus(orderNo, SHIPPED);
        // 记录发货日志
        operationLogService.recordExchangeOperation(
                order.getId(),
                userId,
                OperatorRole.ADMIN,
                "发货",
                String.format("管理员已发货，订单号：%s", orderNo)
        );
    }

    @Override
    @Transactional
    public void confirmReceipt(String orderNo, Long userId) {
        ExchangeOrder order = exchangeMapper.selectByOrderNo(orderNo);
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("订单不存在或不属于当前用户");
        }
        if (order.getStatus() != SHIPPED) {
            throw new IllegalArgumentException("订单状态不允许确认收货");
        }
        exchangeMapper.updateStatus(orderNo, FINISHED);
        // 记录确认收货日志
        operationLogService.recordExchangeOperation(
                order.getId(),
                userId,
                OperatorRole.USER,
                "确认收货",
                String.format("用户已确认收货，订单号：%s", orderNo)
        );
    }

    @Override
    public Long getExchangeCount() {
        return exchangeMapper.getCount();
    }

    @Override
    public int delete(Long id) {
        ExchangeOrder order = exchangeMapper.selectById(id);
        if(CANCELLED.equals(order.getStatus())||FINISHED.equals(order.getStatus())) {
            return exchangeMapper.delete(id);
        }
        return 0;
    }

    @Override
    @Transactional
    public void cancelOrder(String orderNo, Long userId) {
        ExchangeOrder order = exchangeMapper.selectByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("订单不存在或不属于当前用户");
        }

        // 如果订单已支付，需要退款并恢复优惠券
        if (order.getStatus() == PAID) {
            // 退款到用户余额
            userMapper.addBalance(userId, order.getPayAmount());
            // 恢复优惠券状态为未使用
            if (order.getCouponId() != null) {
                couponMapper.restoreCoupon(order.getCouponId());
            }
        } else if (order.getStatus() != UNPAID) {
            throw new IllegalArgumentException("订单状态不允许取消");
        }

        exchangeMapper.updateStatus(orderNo, CANCELLED);
        // 记录取消订单日志
        operationLogService.recordExchangeOperation(
                order.getId(),
                userId,
                OperatorRole.USER,
                "取消订单",
                String.format("取消订单，订单号：%s", orderNo)
        );
    }
}
