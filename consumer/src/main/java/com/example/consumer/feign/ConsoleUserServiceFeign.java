package com.example.consumer.feign;

import com.example.common.annotations.VerifiedUser;
import com.example.common.entity.User;
import com.example.common.utils.Response;
import com.example.provider.controller.domain.user.UserInfoVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="provider", contextId = "consoleUserServiceFeign", path = "/console/user")
public interface ConsoleUserServiceFeign {
    @RequestMapping("/login")
    Response<String> login(@RequestParam(name = "phone") String phone,
                   @RequestParam(name = "password") String password);

    @RequestMapping("/info")
    Response<UserInfoVO> getUserInfo(@VerifiedUser User loginUser);

    @RequestMapping("/register")
    Response<String> register(@RequestParam(name = "phone") String phone,
                     @RequestParam(name = "password") String password,
                     @RequestParam(name = "name") String name,
                     @RequestParam(name = "avatar") String avatar);

    @RequestMapping("/update")
    Response<String> update(@VerifiedUser User loginUser,
                   @RequestParam(name = "phone", required = false) String phone,
                   @RequestParam(name = "password", required = false) String password,
                   @RequestParam(name = "name", required = false) String name,
                   @RequestParam(name = "avatar", required = false) String avatar);


    @RequestMapping("/logout")
    Response<String> logout(HttpServletResponse response);

}
