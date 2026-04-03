package com.demo.controller.admin;

import com.demo.common.Result;
import com.demo.entity.SysUser;
import com.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员-系统用户管理控制器
 */
@RestController
@RequestMapping("api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 条件分页查询所有注册用户
     * @param user 查询条件
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 用户列表分页数据
     */
    @GetMapping
    public Result selectAll(
            SysUser user,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(userService.selectAll(user,pageNum, pageSize));
    }

    /**
     * 禁用或启用指定用户
     * @param id 用户ID
     * @param status 目标状态值
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    public Result updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        int res =  userService.updateStatus(id, status);
        if (res == 1) {
            return Result.success("切换用户状态成功");
        }
        return Result.notFoundId(res);
    }
}
