package com.example.consumer.controller;

import com.example.common.annotations.VerifiedUser;
import com.example.common.entity.User;
import com.example.common.utils.Response;
import com.example.consumer.feign.ConsoleUserServiceFeign;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/console/user")
public class ConsoleUserController {
    @Resource
    private ConsoleUserServiceFeign consoleUserServiceFeign;

    @RequestMapping("/login")
    public Response login(@RequestParam(name = "phone") String phone,
                          @RequestParam(name = "password") String password)
        {
        return consoleUserServiceFeign.login(phone, password);
    }

    @RequestMapping("/info")
    public Response getUserInfo(@VerifiedUser User loginUser)
        {
        return consoleUserServiceFeign.getUserInfo(loginUser);
    }

    @RequestMapping("/register")
    public Response register(@RequestParam(name = "phone") String phone,
                            @RequestParam(name = "password") String password,
                            @RequestParam(name = "name") String name,
                            @RequestParam(name = "avatar") String avatar)
        {
        return consoleUserServiceFeign.register(phone, password, name, avatar);
    }

    @RequestMapping("/update")
    public Response update(@VerifiedUser User loginUser,
                         @RequestParam(name = "phone", required = false) String phone,
                         @RequestParam(name = "password", required = false) String password,
                         @RequestParam(name = "name", required = false) String name,
                         @RequestParam(name = "avatar", required = false) String avatar)
        {
        return consoleUserServiceFeign.update(loginUser, phone, password, name, avatar);
    }

}
