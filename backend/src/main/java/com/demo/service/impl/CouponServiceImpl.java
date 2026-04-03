package com.demo.service.impl;

import com.demo.entity.Coupon;
import com.demo.enums.CouponStatus;
import com.demo.mapper.CouponMapper;
import com.demo.service.CouponService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 优惠券服务实现类
 */
@Service
@Transactional(readOnly = true)
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;

    public CouponServiceImpl(CouponMapper couponMapper) {
        this.couponMapper = couponMapper;
    }

    @Override
    @Transactional
    public int insert(Coupon coupon) {
        return couponMapper.insert(coupon);
    }

    @Override
    public List<Coupon> selectByUserId(Long userId) {
        return couponMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public int useCoupon(Long id, Long sourceRecycleId) {
        // 先检查优惠券状态
        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null || coupon.getStatus() != CouponStatus.UNUSED) {
            throw new IllegalArgumentException("优惠券不可用");
        }

        // 更新优惠券状态为已使用
        return couponMapper.useCoupon(id, sourceRecycleId);
    }

    @Override
    public Coupon selectById(Long id) {
        return couponMapper.selectById(id);
    }
}
