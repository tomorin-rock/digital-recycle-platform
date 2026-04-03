package com.demo.controller.admin;

import com.demo.common.Result;
import com.demo.entity.ExchangeOrder;
import com.demo.service.ExchangeService;
import com.demo.utils.currentUser.CurrentUser;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员-换新订单管理控制器
 */
@RestController
@RequestMapping("api/admin/exchanges")
public class AdminExchangeController {

    private final ExchangeService exchangeService;

    public AdminExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    /**
     * 分页查询所有换新订单
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 换新订单列表分页数据
     */
    @GetMapping
    public Result selectAll(@RequestParam(defaultValue = "1") int pageNum,
                            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(exchangeService.selectAll(pageNum, pageSize));
    }

    /**
     * 管理员操作发货
     * @param id 订单ID
     * @param userId 当前操作管理员ID
     * @return 操作结果
     */
    @PutMapping("/shipping/{id}")
    public Result shipping(@PathVariable Long id, @CurrentUser Long userId) {
        ExchangeOrder order = exchangeService.selectById(id);
        if (order == null) {
            return Result.fail(3101, "订单不存在");
        }
        exchangeService.shipping(order.getOrderNo(), userId);
        return Result.success("发货成功");
    }
}
