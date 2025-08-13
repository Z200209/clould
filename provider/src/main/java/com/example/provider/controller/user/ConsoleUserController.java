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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.Base64;

/**
 * 用户控制器
 */
@Slf4j
@RestController("consoleUserController")
@RequestMapping("/console/user")
public class ConsoleUserController {
    
    @Autowired
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
            throw new RuntimeException("参数错误");
        }

        // 验证手机号是否存在
        User user;
        try {
            user = userService.getUserByPhone(phone);
        } catch (Exception e) {
            throw new RuntimeException("查询用户信息失败: {}",e);
        }

        if (user == null) {
            throw new RuntimeException("手机号不存在");
        }

        // 验证密码
        boolean passwordMatches;
        try {
            passwordMatches = new BCryptPasswordEncoder().matches(password, user.getPassword());
        } catch (Exception e) {
            throw new RuntimeException("密码验证失败: {}", e);
        }

        if (!passwordMatches) {
            throw new RuntimeException("密码错误");
        }

        // 生成签名
        Sign sign = new Sign();
        sign.setId(user.getId());
        int time = (int) (System.currentTimeMillis() / 1000 + 3600 * 3); // 3小时有效期
        sign.setExpirationTime(time);

        String token;
        try {
            token = Base64.getUrlEncoder().encodeToString(
                    JSON.toJSONString(sign).getBytes()
            );
        } catch (Exception e) {
            throw new RuntimeException("生成Token失败: {}", e);
        }

        Cookie cookie = new Cookie("auth_token", token);
        cookie.setMaxAge(3 * 60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return token;
    }

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
            throw new RuntimeException("查询用户是否存在失败: {}", e);
        }

        if (existingUser != null) {
            throw new RuntimeException("用户已存在");
        }

        // 注册用户
        int result;
        try {
            result = userService.register(phone, password, name, avatar);
        } catch (Exception e) {
            throw new RuntimeException("注册用户失败: {}", e);
        }

        if (result == 1) {
            return true;
        } else {
            throw new RuntimeException("注册失败");
        }
    }

    @RequestMapping("/info")
    public UserInfoVO getUserInfo(@RequestParam(name = "userId") BigInteger userId) {
        // 验证用户是否登录
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        User loginUser;
        try {
            loginUser = userService.getUserById(userId);
            if (loginUser == null) {
                throw new RuntimeException("用户不存在");
            }
        } catch (Exception e) {
            throw new RuntimeException("用户信息获取失败: {}", e);
        }
        // 构建用户信息对象
        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setId(loginUser.getId());
        userInfo.setPhone(loginUser.getPhone());
        userInfo.setName(loginUser.getName());
        userInfo.setAvatar(loginUser.getAvatar());

        return userInfo;
    }
    
    /**
     * 退出登录
     */
    @RequestMapping("/logout")
    public boolean logout(HttpServletResponse response) {

            // 清除Cookie
            Cookie cookie = new Cookie("auth_token", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);

            return true;
    }

}
