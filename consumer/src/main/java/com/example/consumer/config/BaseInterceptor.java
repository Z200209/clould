package com.example.consumer.config;

import com.alibaba.fastjson.JSON;
import com.example.common.entity.Sign;
import com.example.common.utils.Response;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Consumer端基础拦截器
 * 用于拦截需要认证的请求，通过调用Provider认证服务验证用户身份
 */
@Slf4j
public class BaseInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        // 获取请求路径
        String requestURI = request.getRequestURI();
        log.debug("BaseInterceptor拦截请求: {}, 方法: {}", requestURI, request.getMethod());

        // 获取认证token（支持Cookie和Sign两种方式）
        String token = getTokenFromRequest(request);
        if (token == null || token.trim().isEmpty()) {
            log.warn("请求未携带认证token: {}", requestURI);
            writeErrorResponse(response, new Response<>(1002, "未登录或登录已过期"));
            return false;
        }
        
        log.debug("BaseInterceptor获取到token: {}", token.substring(0, Math.min(20, token.length())) + "...");

        Sign sign;
        try {
            // 直接解析token - Base64解码和JSON解析
            String jsonStr = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            sign = JSON.parseObject(jsonStr, Sign.class);
        } catch (Exception e) {
            // 处理token解析异常
            log.error("Token解析失败: requestURI={}", requestURI, e);
            writeErrorResponse(response, new Response<>(4004, "认证信息格式错误"));
            return false;
        }

        // 验证token是否过期
        if (isSignExpired(sign)) {
            log.warn("签名已过期: userId={}, expirationTime={}, requestURI={}", 
                    sign.getId(), sign.getExpirationTime(), requestURI);
            writeErrorResponse(response, new Response<>(1002, "登录已过期"));
            return false;
        }

        // 将用户ID存储到请求属性中，供后续使用
        request.setAttribute("userId", sign.getId());
        log.debug("认证通过: userId={}, requestURI={}", sign.getId(), requestURI);
        return true;
    }

    /**
     * 从请求中获取认证token
     * 优先级：Cookie > 请求参数 > Header
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 1. 从Cookie中获取auth_token
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("auth_token".equals(cookie.getName())) {
                    String value = cookie.getValue();
                    if (value != null && !value.trim().isEmpty()) {
                        log.debug("从Cookie获取到auth_token");
                        return value;
                    }
                }
            }
        }

        // 2. 从请求参数中获取sign
        String paramSign = request.getParameter("sign");
        if (paramSign != null && !paramSign.trim().isEmpty()) {
            log.debug("从请求参数获取到sign");
            return paramSign;
        }

        // 3. 从Header中获取sign
        String headerSign = request.getHeader("sign");
        if (headerSign != null && !headerSign.trim().isEmpty()) {
            log.debug("从Header获取到sign");
            return headerSign;
        }

        return null;
    }

    /**
     * 检查签名是否过期
     */
    private boolean isSignExpired(Sign sign) {
        if (sign.getExpirationTime() == null) {
            return true;
        }
        
        int currentTime = (int) (System.currentTimeMillis() / 1000);
        return currentTime > sign.getExpirationTime();
    }

    /**
     * 写入错误响应
     */
    private void writeErrorResponse(HttpServletResponse response, Response<?> errorResponse) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        try (PrintWriter writer = response.getWriter()) {
            writer.write(JSON.toJSONString(errorResponse));
            writer.flush();
        }
    }
}