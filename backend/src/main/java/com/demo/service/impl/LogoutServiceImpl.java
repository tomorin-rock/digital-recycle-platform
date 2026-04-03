package com.demo.service.impl;

import com.demo.service.LogoutService;
import com.demo.utils.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 登出服务实现类
 */
@Service
public class LogoutServiceImpl implements LogoutService {

    private final RedisTemplate<String, String> redisTemplate;

    public LogoutServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 将 JWT 加入黑名单，有效期 = 剩余存活时间
     */
    @Override
    public void logout(String token) {
        if (token == null || !JwtUtil.validateToken(token)) {
            return;
        }

        // 计算 Token 剩余有效时间（秒）
        long expireTime = JwtUtil.getRemainingExpireTime(token);
        if (expireTime > 0) {
            // 将 token 作为 key 存入 Redis 黑名单
            String blackKey = "jwt:blacklist:" + token;
            redisTemplate.opsForValue().set(blackKey, "invalid", expireTime, TimeUnit.SECONDS);
        }
    }

    /**
     * 检查 Token 是否在黑名单中
     */
    @Override
    public boolean isBlacklisted(String token) {
        if (token == null) return false;
        return Boolean.TRUE.equals(redisTemplate.hasKey("jwt:blacklist:" + token));
    }
}