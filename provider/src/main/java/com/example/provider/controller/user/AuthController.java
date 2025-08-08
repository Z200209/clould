package com.example.provider.controller.user;

import com.alibaba.fastjson.JSON;
import com.example.provider.service.user.AuthService;
import com.example.common.entity.Sign;
import com.example.common.entity.User;
import com.example.common.utils.Response;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Provider端认证控制器
 * 提供认证相关的API接口
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 获取Token
     */
    @RequestMapping("/getToken")
    public Response<String> getToken(HttpServletRequest request) {
        try {
            String token = authService.getToken(request);
            if (token != null) {
                return new Response<>(1001, token);
            }
            return new Response<>(4005, "获取Token失败");
        } catch (Exception e) {
            log.error("获取Token异常", e);
            return new Response<>(4004, "获取Token异常");
        }
    }

    /**
     * 解析签名
     */
    @RequestMapping("/analyzeSign")
    public Response<Sign> analyzeSign(@RequestParam("sign") String sign) {
        try {
            if (sign == null || sign.trim().isEmpty()) {
                return new Response<>(4005, "签名参数不能为空");
            }
            
            Sign result = authService.analyzeSign(sign);
            if (result != null) {
                return new Response<>(1001, result);
            }
            return new Response<>(4005, "解析签名失败");
        } catch (Exception e) {
            log.error("解析签名异常", e);
            return new Response<>(4004, "解析签名异常");
        }
    }

    /**
     * 获取用户信息
     */
    @RequestMapping("/getUser")
    public Response<User> getUser(@RequestParam("userId") BigInteger userId) {
        try {
            if (userId == null) {
                return new Response<>(4005, "用户ID不能为空");
            }
            
            User user = authService.getUserById(userId);
            if (user != null) {
                return new Response<>(1001, user);
            }
            return new Response<>(2014, "用户不存在");
        } catch (Exception e) {
            log.error("获取用户信息异常", e);
            return new Response<>(4004, "获取用户信息异常");
        }
    }

    /**
     * 用户登录
     */
    @RequestMapping("/login")
    public Response<String> login(@RequestParam("phone") String phone,
                                 @RequestParam("password") String password) {
        try {
            // 参数验证
            if (phone == null || password == null) {
                return new Response<>(4005, "手机号和密码不能为空");
            }
            
            phone = phone.trim();
            password = password.trim();
            if (phone.isEmpty() || password.isEmpty()) {
                return new Response<>(4005, "手机号和密码不能为空");
            }
            
            String token = authService.login(phone, password);
            if (token != null) {
                return new Response<>(1001, token);
            }
            return new Response<>(1010, "登录失败");
        } catch (Exception e) {
            log.error("登录异常", e);
            return new Response<>(4004, "登录异常");
        }
    }
}