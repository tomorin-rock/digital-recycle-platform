package com.demo.service;

import com.demo.entity.Coupon;

import java.util.List;

/**
 * 优惠券服务接口
 * 提供优惠券的生成、查询以及使用功能
 */
public interface CouponService {
    /**
     * 新增/发放优惠券
     * @param coupon 优惠券实体
     * @return 受影响行数
     */
    int insert(Coupon coupon);

    /**
     * 查询指定用户的所有优惠券
     * @param userId 用户ID
     * @return 优惠券列表
     */
    List<Coupon> selectByUserId(Long userId);

    /**
     * 使用优惠券
     * @param id 优惠券ID
     * @param sourceRecycleId 关联的回收订单ID
     * @return 受影响行数
     */
    int useCoupon(Long id, Long sourceRecycleId);

    /**
     * 根据ID查询优惠券详情
     * @param id 优惠券ID
     * @return 优惠券实体
     */
    Coupon selectById(Long id);
}
