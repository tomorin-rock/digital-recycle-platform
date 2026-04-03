package com.demo.vo;

import com.demo.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应对象
 * 用于封装登录成功后的返回信息，包括身份令牌和用户信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseVo {
    /**
     * 身份验证令牌 (JWT)
     */
    private String token;

    /**
     * 当前登录的用户信息
     */
    private SysUser user;
}
