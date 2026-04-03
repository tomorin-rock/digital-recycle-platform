package com.demo.controller.user;

import com.demo.common.Result;
import com.demo.entity.Coupon;
import com.demo.service.CouponService;
import com.demo.utils.currentUser.CurrentUser;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 用户端-优惠券查询控制器
 */
@RestController
@RequestMapping("api/user/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    /**
     * 查询当前用户名下的所有优惠券
     * @param userId 当前用户ID
     * @return 优惠券列表
     */
    @GetMapping
    public Result selectByUserId(@CurrentUser Long userId) {
        List<Coupon> coupons = couponService.selectByUserId(userId);
        return Result.success(coupons);
    }

}
