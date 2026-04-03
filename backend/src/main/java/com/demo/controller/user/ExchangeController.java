package com.demo.controller.user;

import cn.hutool.core.util.IdUtil;
import com.demo.common.Result;
import com.demo.dto.ExchangeDTO;
import com.demo.entity.Coupon;
import com.demo.entity.ExchangeOrder;
import com.demo.entity.Product;
import com.demo.entity.RecycleOrder;
import com.demo.enums.CouponStatus;
import com.demo.service.CouponService;
import com.demo.service.ExchangeService;
import com.demo.service.ProductService;
import com.demo.service.RecycleService;
import com.demo.utils.currentUser.CurrentUser;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.demo.enums.ExchangeOrderStatus.UNPAID;

/**
 * 用户端-以旧换新流程控制器
 */
@RestController
@RequestMapping("/api/user/exchange")
public class ExchangeController {

    private final ExchangeService exchangeService;
    private final RecycleService recycleService;
    private final ProductService productService;
    private final CouponService couponService;

    public ExchangeController(ExchangeService exchangeService,
                              RecycleService recycleService,
                              ProductService productService,
                              CouponService couponService) {
        this.exchangeService = exchangeService;
        this.recycleService = recycleService;
        this.productService = productService;
        this.couponService = couponService;
    }

    /**
     * 提交以旧换新订单
     *
     * @param request 换新请求DTO (包含回收单ID、新产品ID、优惠券ID、地址ID等)
     * @param userId  当前用户ID
     * @return 创建成功的订单信息或错误提示
     */
    @PostMapping
    public Result submitExchangeOrder(@RequestBody ExchangeDTO request, @CurrentUser Long userId) {
        try {
            // 验证参数
            if (request.getRecycleId() == null || request.getNewProductId() == null) {
                return Result.fail(3100, "请提供回收订单ID和新产品ID");
            }

            // 获取回收订单信息并校验所属关系
            RecycleOrder recycleOrder = recycleService.selectById(request.getRecycleId());
            if (recycleOrder == null) {
                return Result.fail(3101, "回收订单不存在");
            }

            if (!recycleOrder.getUserId().equals(userId)) {
                return Result.fail(3107, "回收订单不属于当前用户");
            }

            // 获取新产品信息
            Product newProduct = productService.selectById(request.getNewProductId());
            if (newProduct == null) {
                return Result.fail(3102, "新产品不存在");
            }

            // 计算价格逻辑
            BigDecimal oldProductValue = recycleOrder.getEstimatePrice(); // 旧产品估值
            BigDecimal newProductPrice = newProduct.getMarketPrice();   // 新产品市场价
            BigDecimal discountAmount = BigDecimal.ZERO; // 优惠券抵扣金额

            // 优惠券校验逻辑
            if (request.getCouponId() != null) {
                Coupon coupon = couponService.selectById(request.getCouponId());
                if (coupon == null || !coupon.getUserId().equals(userId)) {
                    return Result.fail(3103, "优惠券无效或不属于当前用户");
                }
                if (coupon.getStatus() != CouponStatus.UNUSED) {
                    return Result.fail(3104, "优惠券已被使用");
                }
                if (coupon.getExpireTime().isBefore(LocalDateTime.now())) {
                    return Result.fail(3105, "优惠券已过期");
                }
                discountAmount = coupon.getAmount();
            }

            // 计算最终实付金额：新机价格 - 旧机估值 - 优惠券
            BigDecimal finalAmount = newProductPrice.subtract(oldProductValue).subtract(discountAmount);
            if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                finalAmount = BigDecimal.ZERO; // 最低支付0元
            }

            // 构造换新订单实体
            ExchangeOrder order = new ExchangeOrder();
            order.setUserId(userId);
            order.setRecycleId(request.getRecycleId());
            order.setCouponId(request.getCouponId());
            order.setNewProductId(request.getNewProductId());
            order.setAddressId(request.getAddressId());
            order.setProductPrice(newProductPrice);
            order.setDiscountAmount(discountAmount);
            order.setPayAmount(finalAmount);
            order.setStatus(UNPAID);
            order.setOrderNo("E" + IdUtil.getSnowflakeNextIdStr());
            order.setCreateTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());

            int res = exchangeService.insert(order);
            if (res == 1) {
                return Result.success("换新订单提交成功", order);
            }
            return Result.fail(3106, "订单创建失败");
        } catch (Exception e) {
            return Result.fail(5000, "系统错误：" + e.getMessage());
        }
    }

    /**
     * 查询当前用户的换新订单列表
     *
     * @param userId 当前用户ID
     * @return 订单列表
     */
    @GetMapping
    public Result selectByUserId(@CurrentUser Long userId) {
        return Result.success(exchangeService.selectByUserId(userId));
    }

    /**
     * 支付换新订单
     *
     * @param orderNo 订单编号
     * @param userId  当前用户ID
     * @return 支付结果
     */
    @PostMapping("/pay/{orderNo}")
    public Result payOrder(@PathVariable String orderNo, @CurrentUser Long userId) {
        int res = exchangeService.payOrder(orderNo, userId);
        return switch (res) {
            case 1 -> Result.success("支付成功");
            case -1 -> Result.fail(3201, "订单不存在或已过期");
            case -2 -> Result.fail(3202, "余额不足");
            default -> Result.fail(3200, "支付失败");
        };
    }


    /**
     * 取消换新订单
     *
     * @param orderNo 订单编号
     * @param userId  当前用户ID
     * @return 取消结果
     */
    @PutMapping("/cancel")
    public Result cancelOrder(@RequestParam String orderNo, @CurrentUser Long userId) {
        try {
            exchangeService.cancelOrder(orderNo, userId);
            return Result.success("取消成功");
        } catch (Exception e) {
            return Result.fail(3300, "取消失败：" + e.getMessage());
        }
    }

    @PutMapping("/confirm")
    public Result confirmReceipt(@RequestParam String orderNo, @CurrentUser Long userId) {
        try {
            exchangeService.confirmReceipt(orderNo, userId);
            return Result.success("确认收货成功");
        } catch (Exception e) {
            return Result.fail(3400, "确认收货失败：" + e.getMessage());
        }
    }
}
