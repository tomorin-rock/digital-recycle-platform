package com.demo.service.impl;

import cn.hutool.core.util.IdUtil;
import com.demo.entity.Coupon;
import com.demo.entity.RecycleOrder;
import com.demo.enums.CouponStatus;
import com.demo.enums.RecycleOrderStatus;
import com.demo.enums.RecyclePayType;
import com.demo.mapper.CouponMapper;
import com.demo.mapper.ProductMapper;
import com.demo.mapper.RecycleMapper;
import com.demo.mapper.UserMapper;
import com.demo.service.OperationLogService;
import com.demo.service.RecycleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.demo.enums.OperatorRole.*;
import static com.demo.enums.RecycleOrderStatus.*;

/**
 * 回收订单服务实现类
 */
@Service
@Transactional(readOnly = true)
public class RecycleServiceImpl implements RecycleService {

    private final RecycleMapper recycleMapper;
    private final CouponMapper couponMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    private final OperationLogService operationLogService;

    public RecycleServiceImpl(RecycleMapper recycleMapper, 
                              CouponMapper couponMapper, 
                              ProductMapper productMapper, 
                              UserMapper userMapper, 
                              OperationLogService operationLogService) {
        this.recycleMapper = recycleMapper;
        this.couponMapper = couponMapper;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
        this.operationLogService = operationLogService;
    }

    /**
     * 生成并保存等额优惠券
     */
    private void generateCoupon(RecycleOrder order){
        Coupon coupon = new Coupon();
        coupon.setUserId(order.getUserId());
        coupon.setSourceRecycleId(order.getId());
        coupon.setAmount(order.getFinalPrice()); // 最终估价金额
        coupon.setCode("EQUAL-" + IdUtil.getSnowflakeNextIdStr());
        coupon.setExpireTime(LocalDateTime.now().plusDays(15)); // 15天有效
        coupon.setStatus(CouponStatus.UNUSED);
        coupon.setCreateTime(LocalDateTime.now());
        couponMapper.insert(coupon);
    }

    @Override
    public PageInfo<RecycleOrder> selectAll(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<RecycleOrder> orderList = recycleMapper.selectAll();
        return new PageInfo<>(orderList);
    }

    @Override
    public List<RecycleOrder> selectByUserId(Long userId) {
        return recycleMapper.selectByUserId(userId);
    }

    @Override
    public RecycleOrder selectById(Long id) {
        return recycleMapper.selectById(id);
    }

    @Override
    @Transactional
    public int updateStatus(Long id, RecycleOrderStatus status) {
        RecycleOrder order = recycleMapper.selectById(id);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        if (canTransition(order.getStatus(), status)) {
            throw new IllegalArgumentException("状态转换错误");
        }
        // 设置新状态和更新时间
        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());
        return recycleMapper.updateById(order);
    }

    @Override
    @Transactional
    public int insert(RecycleOrder recycleOrder) {
        int result = recycleMapper.insert(recycleOrder);
        if(result > 0){
            // 记录提交订单日志
            operationLogService.recordRecycleOperation(
                    recycleOrder.getId(),
                    recycleOrder.getUserId(),
                    USER,
                    "提交回收订单",
                    String.format("提交回收订单，预估价格：%s元，成色：%s",
                            recycleOrder.getEstimatePrice(),
                            recycleOrder.getGrade())
            );
        }
        return result;
    }

    @Override
    @Transactional
    public void confirmFinalPrice(Long id, Long userId, RecyclePayType payType) {
        RecycleOrder order = recycleMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("订单不存在或不属于当前用户");
        }
        if (!WAIT_CONFIRM.equals(order.getStatus())) {
            throw new IllegalStateException("当前状态不可确认");
        }
        
        order.setPayType(payType);
        
        // 更新状态为 FINISHED
        order.setStatus(FINISHED);
        order.setUpdateTime(LocalDateTime.now());
        
        // 1. 保存订单状态更新
        recycleMapper.updateById(order);
        
        // 2. 根据支付类型处理（优惠券或余额）
        if(payType == RecyclePayType.COUPON){
            generateCoupon(order);
        } else {
            // 现金支付到余额
            userMapper.addBalance(userId, order.getFinalPrice());
        }
        
        // 3. 商品库存加一（回收后库存增加）
        productMapper.incrementStock(order.getProductId());
        
        // 4. 添加操作日志记录
        String detail = String.format("订单确认完成，支付方式：%s，金额：%s元",
                payType.getDescription(),
                order.getFinalPrice());
        operationLogService.recordRecycleOperation(id, userId, USER, "订单确认完成", detail);
    }

    @Override
    @Transactional
    public int cancelOrder(Long id, Long userId) {
        RecycleOrder order = recycleMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("订单不存在或不属于当前用户");
        }
        if (canTransition(order.getStatus(), RecycleOrderStatus.CANCELLED)) {
            throw new IllegalArgumentException("当前状态不可取消");
        }
        int result =  recycleMapper.cancelOrder(id);
        if (result > 0){
            // 记录取消订单日志
            operationLogService.recordRecycleOperation(id, userId, USER, "取消回收订单订单", "用户取消了回收订单");
        }
        return result;
    }

    @Override
    @Transactional
    public void startInspection(Long id,Long userId) {
        RecycleOrder order = recycleMapper.selectById(id);
        if (order == null || !RecycleOrderStatus.SUBMITTED.equals(order.getStatus())) {
            throw new IllegalArgumentException("订单不可进入质检");
        }
        order.setStatus(RecycleOrderStatus.WAITING_INSPECTION);
        order.setUpdateTime(LocalDateTime.now());
        recycleMapper.updateById(order);
        // 记录开始质检日志
        operationLogService.recordRecycleOperation(id, userId , ADMIN, "开始质检", "订单进入质检状态");
    }

    @Override
    @Transactional
    public void finishInspection(Long id, Long userId, BigDecimal finalPrice, String remark) {
        RecycleOrder order = recycleMapper.selectById(id);
        if (order == null ||
                !(RecycleOrderStatus.WAITING_INSPECTION.equals(order.getStatus()) ||
                        RecycleOrderStatus.INSPECTING.equals(order.getStatus()))) {
            throw new IllegalArgumentException("订单不在质检状态");
        }
        order.setRemark(remark);
        order.setFinalPrice(finalPrice);
        order.setStatus(RecycleOrderStatus.WAIT_CONFIRM); // 等待用户确认报价
        order.setUpdateTime(LocalDateTime.now());
        recycleMapper.updateById(order);
        // 记录完成质检日志
        String detail = String.format("完成质检，最终价格：%s元，备注：%s", finalPrice, remark);
        operationLogService.recordRecycleOperation(id, userId, ADMIN, "完成质检", detail);
    }

    @Override
    public int delete(Long id) {
        RecycleOrder order = recycleMapper.selectById(id);
        // 当订单状态为已完成和已取消可以删除
        if (FINISHED.equals(order.getStatus())||CANCELLED.equals(order.getStatus())) {
            return recycleMapper.delete(id);
        }
        return 0;
    }

    @Override
    public Long getRecycleCount() {
        return recycleMapper.getCount();
    }

    /**
     * 内部方法：判断订单状态是否允许转换
     */
    private boolean canTransition(RecycleOrderStatus current, RecycleOrderStatus next) {
        // 允许从任何状态转换到 FINISHED 状态，用于以旧换新流程
        if (next == RecycleOrderStatus.FINISHED) {
            return false;
        }
        // 允许从任何未完成状态取消订单
        if (next == RecycleOrderStatus.CANCELLED) {
            return current == RecycleOrderStatus.FINISHED || current == RecycleOrderStatus.CANCELLED;
        }
        return !switch (current) {
            case SUBMITTED -> next == RecycleOrderStatus.WAITING_INSPECTION;
            case WAITING_INSPECTION -> next == RecycleOrderStatus.INSPECTING;
            case INSPECTING -> next == RecycleOrderStatus.WAIT_CONFIRM;
            case WAIT_CONFIRM -> false; // 已在上面处理取消逻辑
            default -> false;
        };
    }
}