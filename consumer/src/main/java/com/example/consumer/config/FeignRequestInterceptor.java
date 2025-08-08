package com.example.consumer.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign请求拦截器
 * 用于在调用Provider服务时自动传递认证相关的参数和头信息
 */
@Component
public class FeignRequestInterceptor implements RequestInterceptor {
    
    @Override
    public void apply(RequestTemplate template) {
        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            
            // 透传所有认证相关参数
            
            // 1. 透传 sign 参数
            String sign = request.getParameter("sign");
            if (sign != null) {
                template.query("sign", sign);
            }
            
            // 2. 透传 sign header
            String signHeader = request.getHeader("sign");
            if (signHeader != null) {
                template.header("sign", signHeader);
            }
            
            // 3. 透传 Cookie（特别是auth_token）
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                StringBuilder cookieHeader = new StringBuilder();
                for (Cookie cookie : cookies) {
                    if ("auth_token".equals(cookie.getName())) {
                        if (cookieHeader.length() > 0) {
                            cookieHeader.append("; ");
                        }
                        cookieHeader.append(cookie.getName()).append("=").append(cookie.getValue());
                    }
                }
                if (cookieHeader.length() > 0) {
                    template.header("Cookie", cookieHeader.toString());
                }
            }
            
            // 4. 透传用户ID（从BaseInterceptor设置的属性中获取）
            Object userId = request.getAttribute("userId");
            if (userId != null) {
                template.header("X-User-Id", userId.toString());
            }
            
            // 5. 透传其他可能的认证 headers
            String authorization = request.getHeader("Authorization");
            if (authorization != null) {
                template.header("Authorization", authorization);
            }
        }
    }
}