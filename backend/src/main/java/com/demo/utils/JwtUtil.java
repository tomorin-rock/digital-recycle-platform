package com.demo.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    private final static String SECRET = "digital-recycle-platform-jwt-secret-key-2024";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    private static final long EXPIRATION_TIME = 86400000; // 24小时

    //创建token
    public static String createToken(Long id, String role) {
        return Jwts.builder()
                .subject(String.valueOf(id))
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, Jwts.SIG.HS256)
                .compact();
    }

    //解析token获取claims
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("token已过期", e);
        } catch (SignatureException e) {
            throw new RuntimeException("token无效", e);
        } catch (Exception e) {
            throw new RuntimeException("解析token失败", e);
        }
    }

    //从token中获取用户id
    public static Long getUserId(String token) {
        return Long.parseLong(parseToken(token).getSubject());
    }

    //冲token中获取角色
    public static String getRole(String token) {
        return parseToken(token).get("role", String.class);
    }

    //验证token是否有效
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public static long getRemainingExpireTime(String token) {
        try {
            Date expiration = parseToken(token).getExpiration();
            long now = System.currentTimeMillis();
            long expireTime = expiration.getTime();
            return Math.max(0, (expireTime - now) / 1000); // 返回秒
        } catch (Exception e) {
            return 0;
        }
    }
}
