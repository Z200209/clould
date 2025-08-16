package com.example.provider.controller.user;

import com.alibaba.fastjson.JSON;
import com.example.common.entity.Sign;
import com.example.common.entity.User;
import com.example.provider.service.user.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.Base64;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户登录
     */
    @RequestMapping("/login")
    public String login(@RequestParam(name = "phone") String phone,
                        @RequestParam(name = "password") String password) {
        // 参数验证
        password = password.trim();
        phone = phone.trim();
        if (phone.isEmpty() || password.isEmpty()) {
            throw new RuntimeException("参数为空");
        }

        // 验证手机号是否存在
        User user;
        try {
            user = userService.getUserByPhone(phone);
            if (user == null) {
                throw new RuntimeException("手机号不存在");
            }
        } catch (Exception e) {
            throw new RuntimeException("查询用户信息失败", e);
        }

        try {
            user = userService.login(phone, password);
        } catch (Exception e) {
            throw new RuntimeException("登录验证失败", e);
        }

        if (user == null) {
            throw new RuntimeException("登录验证失败");
        }

        // 生成签名
        Sign sign = new Sign();
        sign.setId(user.getId());
        int time = (int) (System.currentTimeMillis() / 1000);
        sign.setExpirationTime(time + 3600 * 3); // 3小时有效期

        String encodedSign;
        try {
            encodedSign = Base64.getUrlEncoder().encodeToString(
                    JSON.toJSONString(sign).getBytes());
        } catch (Exception e) {
            throw new RuntimeException("生成Token失败", e);
        }
        return encodedSign;
    }

    /**
     * 用户注册
     */
    @RequestMapping("/register")
    public boolean register(@RequestParam(name = "phone") String phone,
                            @RequestParam(name = "password") String password,
                            @RequestParam(name = "name") String name,
                            @RequestParam(name = "avatar") String avatar) {
        // 参数验证
        phone = phone.trim();
        password = password.trim();
        if (phone.isEmpty() || password.isEmpty() || name == null || avatar == null) {
            throw new RuntimeException("缺少参数");
        }

        // 验证手机号是否已存在
        User existingUser;
        try {
            existingUser = userService.getUserByPhone(phone);
        } catch (Exception e) {
            throw new RuntimeException("查询用户是否存在失败", e);
        }

        if (existingUser != null) {
            throw new RuntimeException("用户已存在");
        }

        // 注册用户
        int result;
        try {
            result = userService.register(phone, password, name, avatar);
        } catch (Exception e) {
            throw new RuntimeException("注册用户失败", e);
        }

        if (result == 1) {
            return true;
        } else {
            throw new RuntimeException("注册失败");
        }
    }

    /**
     * 更新用户信息
     */
    @RequestMapping("/update")
    public boolean update(@RequestParam(name = "userId") BigInteger userId,
                          @RequestParam(name = "phone", required = false) String phone,
                          @RequestParam(name = "password", required = false) String password,
                          @RequestParam(name = "name", required = false) String name,
                          @RequestParam(name = "avatar", required = false) String avatar) {

        // 参数验证
        if (phone != null) {
            phone = phone.trim();
        }
        if (password != null) {
            password = password.trim();
        }

        User loginUser;
        try {
            loginUser = userService.getUserById(userId);
        } catch (Exception e) {
            throw new RuntimeException("查询用户信息失败", e);
        }
        if (loginUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新用户信息
        if (phone != null && !phone.isEmpty()) {
            loginUser.setPhone(phone);
        }
        if (password != null && !password.isEmpty()) {
            loginUser.setPassword(password);
        }
        if (name != null) {
            loginUser.setName(name);
        }
        if (avatar != null) {
            loginUser.setAvatar(avatar);
        }

        // 提交更新
        int result = 0;
        try {
            result = userService.updateInfo(
                    loginUser.getId(), loginUser.getPhone(), loginUser.getPassword(),
                    loginUser.getName(), loginUser.getAvatar()
            );
        } catch (Exception e) {
            log.error("更新用户信息失败: {}", e.getMessage(), e);
        }

        if (result == 0) {
            throw new RuntimeException("更新用户信息失败");
        }

        return true;
    }

    /**
     * 获取用户信息
     */
    @RequestMapping("/info")
    public User getUserInfo(@RequestParam("userId") BigInteger userId) {
        if (userId == null) {
            throw new RuntimeException("用户ID为空");
        }

        User loginUser;
        try {
            loginUser = userService.getUserById(userId);
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取用户信息失败");
        }

        if (loginUser == null) {
            throw new RuntimeException("用户不存在");
        }

        return loginUser;
    }

    /**
     * 用户登出
     */
    @RequestMapping("/logout")
    public boolean logout(HttpServletResponse response) {
        try {
            Cookie cookie = new Cookie("auth_token", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("登出失败", e);
        }
    }
}