package com.demo.service;

/**
 * 登出与安全令牌管理服务接口
 * 处理用户注销逻辑以及基于 Redis 的 Token 黑名单管理
 */
public interface LogoutService {
    /**
     * 执行用户登出，使当前 Token 失效
     * @param token 待失效的 JWT Token
     */
    void logout(String token);

    /**
     * 检查指定 Token 是否已被列入黑名单
     * @param token 待检查的 Token
     * @return 是否在黑名单中
     */
    boolean isBlacklisted(String token);
}
