package com.example.consumer.controller;

import com.example.common.utils.Response;
import com.example.consumer.feign.AppUserServiceFeign;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/app/user")
public class AppUserController {
    @Resource
    private AppUserServiceFeign appUserServiceFeign;
    @RequestMapping("/login")
    public Response<?> login(@RequestParam("phone") String phone,
                             @RequestParam("password") String password) {
        return appUserServiceFeign.login(phone, password);
    }

    @RequestMapping("/register")
    public Response<?> register(@RequestParam("phone") String phone,
                               @RequestParam("password") String password,
                               @RequestParam("name") String name,
                               @RequestParam("avatar") String avatar) {
        return appUserServiceFeign.register(phone, password, name, avatar);
    }

    @RequestMapping("/update")
    public Response<?> updateUser(@RequestParam(value = "phone", required = false) String phone,
                                 @RequestParam(value = "password", required = false) String password,
                                 @RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value = "avatar", required = false) String avatar) {
        return appUserServiceFeign.updateUser(phone, password, name, avatar);
    }

    @RequestMapping("/info")
    public Response<?> getUserInfo() {
        return appUserServiceFeign.getUserInfo();
    }


}
