package com.example.provider.controller.user;

import com.alibaba.fastjson.JSON;

import com.example.provider.controller.domain.user.UserInfoVO;
import com.example.provider.service.user.UserService;
import com.example.common.annotations.VerifiedUser;
import com.example.common.entity.Sign;
import com.example.common.entity.User;
import com.example.common.utils.Response;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 用户控制器
 */
@Slf4j
@RestController("appUserController")
@RequestMapping("/app/user")
public class AppUserController {
    
    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @RequestMapping("/login")
    public Response login(@RequestParam(name = "phone") String phone,
                          @RequestParam(name = "password") String password) {
        // 参数验证
        password = password.trim();
        phone = phone.trim();
        if (phone.isEmpty() || password.isEmpty()) {
            return new Response(4005);
        }
        
        // 验证手机号是否存在
        User userCheck;
        try {
            userCheck = userService.getUserByPhone(phone);
        } catch (Exception e) {
            log.error("查询用户信息失败: {}", e.getMessage(), e);
            return new Response(4004);
        }
        
        if (userCheck == null) {
            return new Response(2014);
        }
        
        // 登录验证
        User user;
        try {
            user = userService.login(phone, password);
        } catch (Exception e) {
            log.error("登录验证失败: {}", e.getMessage(), e);
            return new Response(4004);
        }
        
        if (user == null) {
            return new Response(1010);
        }
        
        // 生成签名
        Sign sign = new Sign();
        sign.setId(user.getId());
        int time = (int) (System.currentTimeMillis() / 1000);
        sign.setExpirationTime(time + 3600 * 3); // 3小时有效期
        
        String encodedSign;
        try {
            encodedSign = Base64.getUrlEncoder().encodeToString(
                    JSON.toJSONString(sign).getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            log.error("生成Token失败: {}", e.getMessage(), e);
            return new Response(4004);
        }
        
        return new Response(1001, encodedSign);
    }

    /**
     * 用户注册
     */
    @RequestMapping("/register")
    public Response register(@RequestParam(name = "phone") String phone,
                            @RequestParam(name = "password") String password,
                            @RequestParam(name = "name") String name,
                            @RequestParam(name = "avatar") String avatar) {
        // 参数验证
        phone = phone.trim();
        password = password.trim();
        if (phone.isEmpty() || password.isEmpty() || name == null || avatar == null) {
            return new Response(4005);
        }
        
        // 验证手机号是否已存在
        User existingUser;
        try {
            existingUser = userService.getUserByPhone(phone);
        } catch (Exception e) {
            log.error("查询用户是否存在失败: {}", e.getMessage(), e);
            return new Response(4004);
        }
        
        if (existingUser != null) {
            return new Response(2015);
        }
        
        // 注册用户
        int result;
        try {
            result = userService.register(phone, password, name, avatar);
        } catch (Exception e) {
            log.error("注册用户失败: {}", e.getMessage(), e);
            return new Response(4004);
        }
        
        if (result == 1) {
            return new Response(1001, "注册成功");
        } else {
            return new Response(4004);
        }
    }

    /**
     * 更新用户信息
     */
    @RequestMapping("/update")
    public Response update(@VerifiedUser User loginUser,
                           @RequestParam(name = "phone", required = false) String phone,
                           @RequestParam(name = "password", required = false) String password,
                           @RequestParam(name = "name", required = false) String name,
                           @RequestParam(name = "avatar", required = false) String avatar) {
        // 验证用户是否登录
        if (loginUser == null) {
            return new Response(1002);
        }
        
        // 参数验证
        if (phone != null) {
            phone = phone.trim();
        }
        if (password != null) {
            password = password.trim();
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
        int result;
        try {
            result = userService.updateInfo(
                    loginUser.getId(), loginUser.getPhone(), loginUser.getPassword(), 
                    loginUser.getName(), loginUser.getAvatar()
            );
        } catch (Exception e) {
            log.error("更新用户信息失败: {}", e.getMessage(), e);
            return new Response(4004);
        }
        
        if (result == 0) {
            return new Response(4004);
        }
        
        return new Response(1001);
    }
    
    /**
     * 获取用户信息
     */
    @RequestMapping("/info")
    public Response getUserInfo(@VerifiedUser User loginUser) {
        // 验证用户是否登录
        if (loginUser == null) {
            return new Response(1002);
        }

        // 构建用户信息对象
        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setId(loginUser.getId());
        userInfo.setPhone(loginUser.getPhone());
        userInfo.setName(loginUser.getName());
        userInfo.setAvatar(loginUser.getAvatar());
        
        return new Response(1001, userInfo);
    }

    /**
     * 登出
     */
    @RequestMapping("/logout")
    public Response logout(HttpServletResponse response) {
        try {
            Cookie cookie = new Cookie("auth_token", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
            return new Response(1001);
        }
        catch (Exception e) {
            log.error("登出失败: {}", e.getMessage(), e);
            return new Response(4004);
        }

    }
}
