package com.example.consumer.controller;

import com.example.common.annotations.VerifiedUser;
import com.example.common.entity.User;
import com.example.common.utils.Response;
import com.example.consumer.feign.ConsoleUserServiceFeign;
import com.example.provider.controller.domain.user.UserInfoVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@Slf4j
@RestController
@RequestMapping("/console/user")
public class ConsoleUserController {
    @Resource
    private ConsoleUserServiceFeign consoleUserServiceFeign;

    @RequestMapping("/login")
    public Response login(
            @RequestParam(name = "phone") String phone,
            @RequestParam(name = "password") String password) {
        try {
        String token =  consoleUserServiceFeign.login(phone, password);
        return token == null ? new Response(4004, "登录失败") : new Response(1001, token);
        } catch (Exception e) {
            log.error("登录失败：{}", e.getMessage());
            return new Response(4004, "登录失败");
        }
    }

    @RequestMapping("/info")
    public Response getUserInfo(
            @VerifiedUser User loginUser) {
        if (loginUser == null) {
            return new Response(1002);
        }
        try {
            BigInteger userId = loginUser.getId();
            UserInfoVO userInfo = consoleUserServiceFeign.getUserInfo(userId);
            return userInfo == null ? new Response(4004) : new Response(1001, userInfo);
        }catch (Exception e){
            log.error("获取用户信息失败：{}", e.getMessage());
            return new Response(4004, "获取用户信息失败");
        }
    }

    @RequestMapping("/register")
    public Response register(
            @RequestParam(name = "phone") String phone,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "avatar") String avatar) {
        try {
            boolean result = consoleUserServiceFeign.register(phone, password, name, avatar);
            return result ? new Response(1001) : new Response(4004);
        } catch (Exception e) {
            log.error("注册失败：{}", e.getMessage());
            return new Response(4004, "注册失败");
        }
    }

    @RequestMapping("/update")
    public Response update(
            @VerifiedUser User loginUser,
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "password", required = false) String password,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "avatar", required = false) String avatar) {
        if (loginUser == null) {
            return new Response(1002);
        }
        try {
            boolean result = consoleUserServiceFeign.update(loginUser, phone, password, name, avatar);
            return result ? new Response(1001) : new Response(4004);
        } catch (Exception e) {
            log.error("更新用户信息失败：{}", e.getMessage());
            return new Response(4004, "更新用户信息失败");
        }
    }

}
