package com.example.provider.service.user;

import com.alibaba.fastjson.JSON;
import com.example.common.annotations.DataSource;
import com.example.common.config.mysql.DataSourceType;
import com.example.common.entity.Sign;
import com.example.common.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Provider端认证服务
 * 处理认证相关的业务逻辑
 */
@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserService userService;

    /**
     * 获取Token
     * 从Cookie、参数或Header中获取认证token
     */
    public String getToken(HttpServletRequest request) {
        try {
            // 1. 从Cookie中获取
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName()) || "sign".equals(cookie.getName())) {
                        String value = cookie.getValue();
                        if (value != null && !value.trim().isEmpty()) {
                            log.info("从Cookie获取到token: {}", value);
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
                log.info("从参数获取到token: {}", paramToken);
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
                log.info("从Header获取到token: {}", headerToken);
                return headerToken;
            }

            log.warn("未找到token");
            return null;
        } catch (Exception e) {
            log.error("获取token失败", e);
            return null;
        }
    }

    /**
     * 解析签名
     * 将Base64编码的签名解码并解析为Sign对象
     */
    public Sign analyzeSign(String sign) {
        try {
            if (sign == null || sign.trim().isEmpty()) {
                log.warn("签名为空");
                return null;
            }

            // Base64解码
            byte[] decodedBytes = Base64.getUrlDecoder().decode(sign);
            String decodedStr = new String(decodedBytes, StandardCharsets.UTF_8);
            log.info("解码后的签名: {}", decodedStr);

            // JSON解析
            Sign signObj = JSON.parseObject(decodedStr, Sign.class);
            if (signObj == null) {
                log.warn("签名解析失败");
                return null;
            }

            log.info("签名解析成功: userId={}, expirationTime={}", signObj.getId(), signObj.getExpirationTime());
            return signObj;
        } catch (Exception e) {
            log.error("解析签名失败", e);
            return null;
        }
    }

    /**
     * 检查签名是否过期
     */
    public boolean checkExpired(Sign sign) {
        if (sign == null) {
            return true;
        }
        int currentTime = (int) (System.currentTimeMillis() / 1000);
        boolean expired = sign.getExpirationTime() < currentTime;
        log.info("签名过期检查: currentTime={}, expirationTime={}, expired={}", 
                currentTime, sign.getExpirationTime(), expired);
        return expired;
    }

    /**
     * 根据用户ID获取用户信息
     */
    @DataSource(DataSourceType.SLAVE)
    public User getUserById(BigInteger userId) {
        try {
            if (userId == null) {
                log.warn("用户ID为空");
                return null;
            }

            User user = userService.getUserById(userId);
            if (user != null) {
                log.info("获取用户信息成功: userId={}, phone={}", user.getId(), user.getPhone());
            } else {
                log.warn("用户不存在: userId={}", userId);
            }
            return user;
        } catch (Exception e) {
            log.error("获取用户信息失败: userId={}", userId, e);
            return null;
        }
    }

    /**
     * 用户登录
     */
    public String login(String phone, String password) {
        try {
            // 验证手机号是否存在
            User userCheck = userService.getUserByPhone(phone);
            if (userCheck == null) {
                log.warn("手机号不存在: {}", phone);
                return null;
            }

            // 登录验证
            User user = userService.login(phone, password);
            if (user == null) {
                log.warn("登录验证失败: {}", phone);
                return null;
            }

            // 生成签名
            Sign sign = new Sign();
            sign.setId(user.getId());
            int time = (int) (System.currentTimeMillis() / 1000);
            sign.setExpirationTime(time + 3600 * 3); // 3小时有效期

            String encodedSign = Base64.getUrlEncoder().encodeToString(
                    JSON.toJSONString(sign).getBytes(StandardCharsets.UTF_8)
            );

            log.info("用户登录成功: userId={}, phone={}", user.getId(), phone);
            return encodedSign;
        } catch (Exception e) {
            log.error("登录失败: phone={}", phone, e);
            return null;
        }
    }
}