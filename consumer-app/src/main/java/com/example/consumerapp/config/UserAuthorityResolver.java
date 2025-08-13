package com.example.consumerapp.config;

import com.example.common.annotations.VerifiedUser;
import com.example.common.entity.Sign;
import com.example.common.entity.User;
import com.example.consumerapp.feign.AuthServiceFeign;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Consumer端用户认证参数解析器
 * 用于解析@VerifiedUser注解标注的参数，通过调用Provider认证服务获取用户信息
 */
@Slf4j
public class UserAuthorityResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private AuthServiceFeign authServiceFeign;
    
    @Autowired
    private ObjectMapper objectMapper;

    private final ApplicationArguments appArguments;

    public UserAuthorityResolver(ApplicationArguments appArguments) {
        this.appArguments = appArguments;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 支持带有@VerifiedUser注解且类型为User的参数
        return parameter.hasParameterAnnotation(VerifiedUser.class) && 
               parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, 
                                ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest, 
                                WebDataBinderFactory binderFactory) throws Exception {
        
        log.debug("UserAuthorityResolver.resolveArgument 被调用");
        
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            log.warn("无法获取HttpServletRequest");
            return null;
        }

        log.debug("请求URI: {}, 方法: {}", request.getRequestURI(), request.getMethod());

        // 获取认证token
        String token = getTokenFromRequest(request);
        if (token == null || token.trim().isEmpty()) {
            log.warn("未找到认证token，请求URI: {}", request.getRequestURI());
            return null;
        }
        
        log.debug("获取到token: {}", token.substring(0, Math.min(20, token.length())) + "...");

        try {
            // 本地解析token - Base64解码和JSON解析
            String jsonStr = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            Sign sign = objectMapper.readValue(jsonStr, Sign.class);
            
            // 验证token是否过期
            if (isSignExpired(sign)) {
                log.warn("签名已过期: userId={}, expirationTime={}", 
                        sign.getId(), sign.getExpirationTime());
                return null;
            }
            
            // 通过Provider认证服务获取完整用户信息
            log.debug("准备调用authServiceFeign.getUser，userId: {}", sign.getId());
            var userResponse = authServiceFeign.getUser(sign.getId());
            log.debug("authServiceFeign.getUser响应: {}", userResponse);
            
            if (userResponse == null) {
                log.warn("获取用户信息失败: userId={}, 响应为空", sign.getId());
                return null;
            }
            
            if (userResponse.getStatus().getCode() != 1001) {
                log.warn("获取用户信息失败: userId={}, 状态码={}, 消息={}", 
                        sign.getId(), userResponse.getStatus().getCode(), userResponse.getStatus().getMsg());
                return null;
            }
            
            if (userResponse.getResult() == null) {
                log.warn("获取用户信息失败: userId={}, result为空", sign.getId());
                return null;
            }

            User user = userResponse.getResult();
            log.info("用户认证成功: userId={}, phone={}, name={}", user.getId(), user.getPhone(), user.getName());
            return user;
            
        } catch (Exception e) {
            log.error("Token解析失败", e);
            return null;
        }
    }

    /**
     * 从请求中获取认证token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 1. 从Cookie中获取
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("auth_token".equals(cookie.getName()) || "token".equals(cookie.getName()) || "sign".equals(cookie.getName())) {
                    String value = cookie.getValue();
                    if (value != null && !value.trim().isEmpty()) {
                        log.debug("从Cookie获取到token");
                        return value;
                    }
                }
            }
        }

        // 2. 从请求参数中获取
        String paramToken = request.getParameter("token");
        if (paramToken == null) {
            paramToken = request.getParameter("sign");
        }
        if (paramToken != null && !paramToken.trim().isEmpty()) {
            log.debug("从请求参数获取到token");
            return paramToken;
        }

        // 3. 从Header中获取
        String headerToken = request.getHeader("Authorization");
        if (headerToken == null) {
            headerToken = request.getHeader("token");
        }
        if (headerToken == null) {
            headerToken = request.getHeader("sign");
        }
        if (headerToken != null && !headerToken.trim().isEmpty()) {
            log.debug("从Header获取到token");
            return headerToken;
        }

        return null;
    }

    /**
     * 检查签名是否过期
     */
    private boolean isSignExpired(Sign sign) {
        if (sign == null || sign.getExpirationTime() == null) {
            return true;
        }
        long currentTime = System.currentTimeMillis() / 1000; // 转换为秒
        return currentTime > sign.getExpirationTime();
    }
}