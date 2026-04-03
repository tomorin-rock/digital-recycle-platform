package com.demo.mapper;

import com.demo.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CouponMapper {
    int insert(Coupon coupon);

    List<Coupon> selectByUserId(Long userId);

    int useCoupon(Long id, Long sourceRecycleId);

    Coupon selectById(Long id);

    void restoreCoupon(Long couponId);
}
