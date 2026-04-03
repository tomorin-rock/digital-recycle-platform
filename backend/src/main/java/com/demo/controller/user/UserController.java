package com.demo.controller.user;

import com.demo.common.Result;
import com.demo.dto.ResetDTO;
import com.demo.service.LogoutService;
import com.demo.service.UserService;
import com.demo.service.FileStorageService;
import com.demo.utils.currentUser.CurrentUser;
import com.demo.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 用户端-个人信息与账户管理控制器
 */
@RestController
@RequestMapping("/api/user/me")
@Slf4j
public class UserController {

    private final UserService userService;
    private final LogoutService logoutService;
    private final FileStorageService fileStorageService;

    public UserController(UserService userService, 
                          LogoutService logoutService, 
                          FileStorageService fileStorageService) {
        this.userService = userService;
        this.logoutService = logoutService;
        this.fileStorageService = fileStorageService;
    }

    /**
     * 用户申请注销账户
     * @param id 当前用户ID
     * @param request 请求对象（用于获取并作废Token）
     * @return 操作结果
     */
    @PostMapping("/cancel")
    public Result cancel(@CurrentUser Long id, HttpServletRequest request) {
        try {
            // 1. 作废当前 Token（加入黑名单）
            String token = JwtUtil.resolveToken(request);
            if (token != null) {
                logoutService.logout(token);
            }
            // 2. 标记用户注销状态
            userService.delete(id);
            return Result.success("用户已注销，数据将在30天内清除");
        } catch (IllegalArgumentException e) {
            return Result.fail(2001, e.getMessage());
        }
    }

    /**
     * 修改账户登录密码
     * @param id 当前用户ID
     * @param dto 包含旧密码、新密码、确认密码的DTO
     * @return 操作结果
     */
    @PutMapping("/password")
    public Result updatePassword(@CurrentUser Long id, @Valid @RequestBody ResetDTO dto) {
        int res = userService.updatePassword(id, dto.getOldPwd(), dto.getNewPwd(), dto.getConfirmPwd());
        return switch (res) {
            case -1 -> Result.fail(2001, "用户不存在");
            case -2 -> Result.fail(2002, "旧密码不正确");
            case -3 -> Result.fail(2003, "新密码和旧密码相同");
            case -4 -> Result.fail(2004, "新密码和确认密码不相同");
            default -> Result.success("密码修改成功");
        };
    }

    /**
     * 修改个人基本信息（昵称等）
     * @param id 当前用户ID
     * @param dto 包含昵称等信息的DTO
     * @return 操作结果
     */
    @PutMapping
    public Result updateInfo(@CurrentUser Long id,
                             @RequestBody ResetDTO dto) {
        userService.updateInfo(id, dto.getNickname(), dto.getAmount());
        return Result.success("信息修改完成");
    }

    /**
     * 查询当前登录用户的详细信息
     * @param id 当前用户ID
     * @return 用户详细信息实体
     */
    @GetMapping
    public Result selectById(@CurrentUser Long id) {
        return Result.success(userService.selectById(id));
    }

    /**
     * 上传或更换个人头像
     * @param file 头像图片文件
     * @param userId 当前用户ID
     * @return 包含新头像URL的成功响应
     */
    @PostMapping("/avatar")
    public Result uploadAvatar(@RequestParam("file") MultipartFile file,
                               @CurrentUser Long userId) {
        try {
            // 1. 基础验证
            if (file.isEmpty()) {
                return Result.fail(4001, "请选择要上传的文件");
            }
            if (!fileStorageService.validateFileType(file)) {
                return Result.fail(4002, "只支持 JPG、PNG、GIF 格式的图片");
            }
            if (!fileStorageService.validateFileSize(file)) {
                return Result.fail(4003, "图片大小不能超过10MB");
            }
            
            // 2. 保存文件并同步数据库
            String avatarUrl = fileStorageService.saveFile(file, "avatar");
            userService.updateAvatar(userId, avatarUrl);

            log.info("用户 {} 头像上传成功，URL: {}", userId, avatarUrl);
            return Result.success("头像上传成功", avatarUrl);
        } catch (IOException e) {
            log.error("头像上传失败，用户ID: {}", userId, e);
            return Result.fail(4000, "头像保存失败");
        }
    }
}
