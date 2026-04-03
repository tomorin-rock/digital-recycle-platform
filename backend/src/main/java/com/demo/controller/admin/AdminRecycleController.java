package com.demo.controller.admin;

import com.demo.common.Result;
import com.demo.dto.InspectDTO;
import com.demo.service.RecycleService;
import com.demo.utils.currentUser.CurrentUser;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 管理员-回收订单质检管理控制器
 */
@RestController
@RequestMapping("api/admin/recycles")
public class AdminRecycleController {

    private final RecycleService recycleService;

    public AdminRecycleController(RecycleService recycleService) {
        this.recycleService = recycleService;
    }

    /**
     * 分页查询所有回收订单
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 回收订单列表分页数据
     */
    @GetMapping
    public Result selectAll(@RequestParam(defaultValue = "1") int pageNum, 
                            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(recycleService.selectAll(pageNum, pageSize));
    }

    /**
     * 将订单移入质检队列
     * @param id 订单ID
     * @param userId 当前管理员ID
     * @return 操作结果
     */
    @PostMapping("startInspection/{id}")
    public Result startInspection(@PathVariable Long id, @CurrentUser Long userId) {
        recycleService.startInspection(id, userId);
        return Result.success("成功进入质检队列");
    }

    /**
     * 录入质检结果并完成质检
     * @param id 订单ID
     * @param userId 当前管理员ID
     * @param dto 包含最终定价和备注的质检信息
     * @return 操作结果
     */
    @PostMapping("finishInspection/{id}")
    public Result finishInspection(@PathVariable Long id, 
                                   @CurrentUser Long userId, 
                                   @RequestBody InspectDTO dto) {
        if (dto.getFinalPrice() == null || dto.getFinalPrice().compareTo(BigDecimal.ZERO) < 0) {
            return Result.fail(4032, "最终价格无效");
        }
        recycleService.finishInspection(id, userId, dto.getFinalPrice(), dto.getRemark());
        return Result.success("质检完成，等待用户确认");
    }
}
