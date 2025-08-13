package com.example.provider.config;

import com.example.common.annotations.VerifiedUser;
import com.example.common.entity.User;
import com.example.provider.service.user.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.math.BigInteger;
import java.util.List;

/**
 * Provider端Web MVC配置
 * 配置参数解析器处理@VerifiedUser注解，接收Consumer端传递的用户信息
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    
    @Autowired
    private AuthService authService;
    
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new ProviderUserAuthorityResolver(authService));
    }
    
    /**
     * Provider端用户参数解析器
     * 从Consumer端传递的请求头中获取用户ID，然后查询用户信息
     */
    @Slf4j
    public static class ProviderUserAuthorityResolver implements HandlerMethodArgumentResolver {
        
        private final AuthService authService;
        
        public ProviderUserAuthorityResolver(AuthService authService) {
            this.authService = authService;
        }
        
        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.hasParameterAnnotation(VerifiedUser.class) && 
                   parameter.getParameterType().equals(User.class);
        }
        
        @Override
        public Object resolveArgument(@NotNull MethodParameter parameter,
                                      ModelAndViewContainer mavContainer,
                                      @NotNull NativeWebRequest webRequest,
                                      WebDataBinderFactory binderFactory) {
            try {
                // 从请求头中获取用户ID
                HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
                String userIdHeader = null;
                if (request != null) {
                    userIdHeader = request.getHeader("X-User-Id");
                }

                if (userIdHeader == null || userIdHeader.trim().isEmpty()) {
                    log.warn("Provider端未接收到用户ID信息");
                    return null;
                }
                
                // 解析用户ID并获取用户信息
                BigInteger userId = new BigInteger(userIdHeader.trim());
                User user = authService.getUserById(userId);
                
                if (user != null) {
                    log.debug("Provider端成功获取用户信息: userId={}, phone={}", user.getId(), user.getPhone());
                } else {
                    log.warn("Provider端未找到用户信息: userId={}", userId);
                }
                
                return user;
            } catch (Exception e) {
                log.error("Provider端解析用户信息失败", e);
                return null;
            }
        }
    }
}