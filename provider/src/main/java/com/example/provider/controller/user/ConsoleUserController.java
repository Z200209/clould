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
    public Response login(@RequestParam(name = "phone") String phone,
                          @RequestParam(name = "password") String password) {
        // 参数验证
        password = password.trim();
        phone = phone.trim();
        if (phone.isEmpty() || password.isEmpty()) {
            return new Response(4005);
        }

        // 验证手机号是否存在
        User user;
        try {
            user = userService.getUserByPhone(phone);
        } catch (Exception e) {
            log.error("查询用户信息失败: {}", e.getMessage(), e);
            return new Response(4004);
        }

        if (user == null) {
            return new Response(2014);
        }

        // 验证密码
        boolean passwordMatches;
        try {
            passwordMatches = new BCryptPasswordEncoder().matches(password, user.getPassword());
        } catch (Exception e) {
            log.error("密码验证失败: {}", e.getMessage(), e);
            return new Response(4004);
        }

        if (!passwordMatches) {
            return new Response(1010);
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
            log.error("生成Token失败: {}", e.getMessage(), e);
            return new Response(4004);
        }

        Cookie cookie = new Cookie("auth_token", token);
        cookie.setMaxAge(3 * 60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);


        return new Response(1001 , token);

    }
    @RequestMapping("/info")
    public Response getUserInfo(User loginUser) {
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
     * 退出登录
     */
    @RequestMapping("/logout")
    public Response logout(HttpServletResponse response) {

            // 清除Cookie
            Cookie cookie = new Cookie("auth_token", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);

            return new Response(1001);
    }

}
