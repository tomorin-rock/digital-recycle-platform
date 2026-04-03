package com.demo.controller.admin;


import com.demo.common.Result;
import com.demo.service.ExchangeService;
import com.demo.service.ProductService;
import com.demo.service.RecycleService;
import com.demo.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员-控制面板数据控制器
 */
@RestController
@RequestMapping("api/admin/dashboard")
public class AdminDashBoardController {

    private final UserService userService;
    private final ExchangeService exchangeService;
    private final RecycleService recycleService;
    private final ProductService productService;

    public AdminDashBoardController(UserService userService, 
                                   ExchangeService exchangeService, 
                                   RecycleService recycleService, 
                                   ProductService productService) {
        this.userService = userService;
        this.exchangeService = exchangeService;
        this.recycleService = recycleService;
        this.productService = productService;
    }

    /**
     * 获取后台首页统计数据
     * @return 包含用户数、换新单数、回收单数、商品总数的统计 Map
     */
    @GetMapping("count")
    public Result getCounts(){
        Map<String, Long> counts = new HashMap<>();
        counts.put("userCount", userService.getUserCount());
        counts.put("exchangeCount", exchangeService.getExchangeCount());
        counts.put("recycleCount", recycleService.getRecycleCount());
        counts.put("productCount", productService.getProductCount());
        return Result.success(counts);
    }
}
