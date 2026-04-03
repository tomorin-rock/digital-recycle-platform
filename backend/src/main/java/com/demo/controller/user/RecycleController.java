package com.demo.controller.user;

import cn.hutool.core.util.IdUtil;
import com.demo.common.Result;
import com.demo.dto.EstimateDTO;
import com.demo.dto.RecycleDTO;
import com.demo.entity.Product;
import com.demo.entity.RecycleOrder;
import com.demo.enums.ProductGrade;
import com.demo.enums.RecyclePayType;
import com.demo.service.ProductService;
import com.demo.service.RecycleService;
import com.demo.utils.currentUser.CurrentUser;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.demo.enums.RecycleOrderStatus.SUBMITTED;
import static com.demo.utils.CalculateUtils.calculateEstimatePrice;


/**
 * 用户端-旧机回收流程控制器
 */
@RestController
@RequestMapping("/api/user/recycles")
public class RecycleController {

    private final RecycleService recycleService;
    private final ProductService productService;

    public RecycleController(RecycleService recycleService, ProductService productService) {
        this.recycleService = recycleService;
        this.productService = productService;
    }

    /**
     * 根据产品和成色获取回收估价
     * @param dto 估价请求DTO (包含产品ID和成色等级)
     * @return 估算后的价格
     */
    @PostMapping("/estimate")
    public Result estimatePrice(@RequestBody EstimateDTO dto) {
        try {
            // 1. 获取产品基准价格
            Product product = productService.selectById(dto.getProductId());
            if (product == null) {
                return Result.fail(2001, "产品不存在");
            }

            // 2. 根据成色计算估算价格
            BigDecimal basePrice = product.getRecycleBasePrice();
            BigDecimal estimatePrice = calculateEstimatePrice(basePrice, dto.getGrade());

            return Result.success(estimatePrice);
        } catch (Exception e) {
            return Result.fail(5000, "估价失败：" + e.getMessage());
        }
    }

    /**
     * 用户提交回收申请订单
     * @param request 回收请求DTO
     * @param userId 当前用户ID
     * @return 创建成功的订单实体
     */
    @PostMapping
    public Result submitRecycleOrder(@RequestBody RecycleDTO request, @CurrentUser Long userId) {
        try {
            // 1. 构造回收订单
            RecycleOrder order = new RecycleOrder();
            order.setUserId(userId);
            order.setProductId(request.getProductId());
            order.setAddressId(request.getAddressId());

            // 2. 记录产品快照（防止后续商品改名影响订单）
            Product product = productService.selectById(request.getProductId());
            if (product != null) {
                order.setProductSnapshot(product.getModelName());
            }

            order.setGrade(ProductGrade.valueOf(request.getGrade()));
            order.setAppearanceDesc(request.getAppearanceDesc());
            order.setFunctionDesc(request.getFunctionDesc());
            order.setFinalPrice(null); // 初始最终价为空，待质检

            // 3. 设置订单基本信息
            order.setEstimatePrice(request.getEstimatePrice());
            order.setStatus(SUBMITTED);
            order.setCreateTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            order.setOrderNo("R" + IdUtil.getSnowflakeNextIdStr());

            // 4. 执行持久化
            int result = recycleService.insert(order);
            if (result > 0) {
                return Result.success("回收订单提交成功", order);
            } else {
                return Result.fail(3002, "回收订单提交失败");
            }
        } catch (Exception e) {
            return Result.fail(5000, "系统错误：" + e.getMessage());
        }
    }

    /**
     * 用户确认质检后的最终价格
     * @param id 订单ID
     * @param payType 支付方式（余额/优惠券）
     * @param userId 当前用户ID
     * @return 操作结果
     */
    @PostMapping("/confirm/{id}")
    public Result confirmFinalPrice(@PathVariable Long id,
                                    @RequestParam(required = false) RecyclePayType payType,
                                    @CurrentUser Long userId) {
        recycleService.confirmFinalPrice(id, userId, payType);
        return payType == RecyclePayType.COUPON ? 
               Result.success("确认成功，已发放优惠券") : 
               Result.success("确认成功，资金已打入余额");
    }

    /**
     * 用户取消订单
     * @param id 订单ID
     * @param userId 当前用户ID
     * @return 操作结果
     */
    @PutMapping("/cancel/{id}")
    public Result cancelOrder(@PathVariable Long id, @CurrentUser Long userId) {
        return Result.success(recycleService.cancelOrder(id, userId));
    }

    /**
     * 查询当前用户的所有回收订单
     * @param userId 当前用户ID
     * @return 订单列表
     */
    @GetMapping
    public Result selectByUserId(@CurrentUser Long userId) {
        return Result.success(recycleService.selectByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        return Result.success(recycleService.delete(id));
    }
}

