package com.demo.filter;

import com.demo.service.impl.LogoutServiceImpl; // 新增导入
import com.demo.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final LogoutServiceImpl logoutService;

    //服务
    public JwtAuthenticationFilter(LogoutServiceImpl logoutService) {
        this.logoutService = logoutService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                    @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String token = resolveToken(request);

        if (token != null) {
            // ✅ 新增：检查黑名单
            if (logoutService.isBlacklisted(token)) {
                SecurityContextHolder.clearContext();
                // 可选：返回 401，但通常由全局异常处理
                filterChain.doFilter(request, response);
                return;
            }

            if (JwtUtil.validateToken(token)) {
                try {
                    Long userId = JwtUtil.getUserId(token);
                    String role = JwtUtil.getRole(token);
                    String authority = "ROLE_" + role;
                    var auth = new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority(authority))
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (Exception e) {
                    SecurityContextHolder.clearContext();
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        return JwtUtil.resolveToken(request);
    }
}