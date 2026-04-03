package com.demo.controller;

import com.demo.common.Result;
import com.demo.dto.LoginDTO;
import com.demo.dto.RegisterDTO;
import com.demo.entity.SysUser;
import com.demo.service.impl.LogoutServiceImpl;
import com.demo.service.UserService;
import com.demo.utils.JwtUtil;
import com.demo.vo.LoginResponseVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.demo.utils.JwtUtil.resolveToken;

/**
 * 认证授权控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final LogoutServiceImpl logoutService;

    public AuthController(UserService userService, LogoutServiceImpl logoutService) {
        this.userService = userService;
        this.logoutService = logoutService;
    }

    /**
     * 用户注册
     * @param dto 注册请求实体
     * @return 注册成功的用户信息或错误提示
     */
    @PostMapping("/register")
    public Result register(@Valid @RequestBody RegisterDTO dto) {
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setNickname(dto.getNickname());
        
        int registerUser = userService.register(user, dto.getConfirmPwd());
        if (registerUser == 1) {
            return Result.success("注册成功", user);
        } else if (registerUser == -1) {
            return Result.fail(1001, "用户名已存在");
        } else if (registerUser == 0) {
            return Result.fail(1002, "两次输入的密码不一致");
        }
        return Result.fail(1000, "注册失败");
    }

    /**
     * 用户登录
     * @param dto 登录请求实体
     * @return 包含 Token 和用户信息的登录结果
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO dto) {
        String loginId = dto.getUsername();
        String password = dto.getPassword();

        // 1. 基础校验
        if (loginId == null || loginId.isBlank() || password == null || password.isBlank()) {
            return Result.fail(1001, "账号或密码不能为空");
        }

        // 2. 调用服务层验证
        SysUser user = userService.login(loginId, password);

        // 3. 处理登录结果
        if (user == null) {
            return Result.fail(1002, "用户名或密码错误");
        }
        
        // 4. 账号状态检查
        if (user.getStatus() != 1) {
            return Result.fail(1003, "账号已被禁用，请联系管理员");
        }

        // 5. 生成 Token 并返回
        String token = JwtUtil.createToken(user.getId(), user.getRole().name());
        LoginResponseVo res = new LoginResponseVo(token, user);
        return Result.success("登录成功", res);
    }

    /**
     * 用户注销登录
     * @param request 请求对象（用于提取Token）
     * @return 操作结果
     */
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        String token = resolveToken(request);
        if (token != null) {
            logoutService.logout(token);
        }
        return Result.success("退出成功");
    }
}
