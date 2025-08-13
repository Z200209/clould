package com.example.consumerapp.controller;

import com.example.common.annotations.VerifiedUser;
import com.example.common.entity.User;
import com.example.common.utils.Response;

import com.example.consumerapp.feign.AppUserServiceFeign;
import com.example.provider.controller.domain.user.UserInfoVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@Slf4j
@RestController
@RequestMapping("/app/user")
public class AppUserController {
    @Resource
    private AppUserServiceFeign appUserServiceFeign;
    @RequestMapping("/login")
    public Response<?> login(@RequestParam("phone") String phone,
                             @RequestParam("password") String password) {
        try {
            String token = appUserServiceFeign.login(phone, password);
            if (token == null){
                return new Response<>(4004, "登录失败");
            }
            return new Response<>(1001, token);
        } catch (Exception e) {
            log.error("登录失败：{}", e.getMessage());
            return new Response<>(4004, "登录失败");
        }
    }

    @RequestMapping("/register")
    public Response<?> register(@RequestParam("phone") String phone,
                               @RequestParam("password") String password,
                               @RequestParam("name") String name,
                               @RequestParam("avatar") String avatar) {
        try {
         boolean result = appUserServiceFeign.register(phone, password, name, avatar);
         return result ? new Response<>(1001) : new Response<>(4004);
        } catch (Exception e) {
            log.error("注册失败：{}", e.getMessage());
            return new Response<>(4004, "注册失败");
        }
    }

    @RequestMapping("/update")
    public Response<?> updateUser(
            @VerifiedUser User loginUser,
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "password", required = false) String password,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "avatar", required = false) String avatar) {
        if (loginUser == null) {
            return new Response(1002);
        }
        BigInteger userId = loginUser.getId();
        try {
        boolean result =  appUserServiceFeign.updateUser(userId,phone, password, name, avatar);
        return result ? new Response(1001) : new Response(4004);
    }
        catch (Exception e) {
            log.error("更新用户信息失败：{}", e.getMessage());
            return new Response<>(4004, "更新用户信息失败");
        }
    }

    @RequestMapping("/info")
    public Response<?> getUserInfo(
            @VerifiedUser User loginUser
    ) {
        if (loginUser == null) {
            return new Response<>(1002);
        }
        BigInteger userId = loginUser.getId();
        try {
        UserInfoVO userInfo = appUserServiceFeign.getUserInfo(userId);
        return new Response<>(1001, userInfo);
    } catch (Exception e) {
            log.error("获取用户信息失败：{}", e.getMessage());
            return new Response<>(4004, "获取用户信息失败");
        }
    }


}
