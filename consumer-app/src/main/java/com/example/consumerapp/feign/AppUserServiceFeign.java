package com.example.consumerapp.feign;

import com.example.provider.controller.domain.user.UserInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;

/**
 * 用户服务Feign客户端
 * 用于调用用户微服务的远程接口
 */
@FeignClient(name = "provider" , contextId = "appUserServiceFeign", path = "/app/user")
public interface AppUserServiceFeign {
    /**
     * App端用户登录
     *
     * @param phone    手机号
     * @param password 密码
     * @return 登录结果（包含token）
     */
    @RequestMapping("/login")
    String login(
            @RequestParam("phone") String phone,
            @RequestParam("password") String password);

    /**
     * App端用户注册
     *
     * @param phone    手机号
     * @param password 密码
     * @param name     用户名
     * @param avatar   头像URL
     * @return 注册结果
     */
    @RequestMapping("/register")
    boolean register(
            @RequestParam("phone") String phone,
            @RequestParam("password") String password,
            @RequestParam("name") String name,
            @RequestParam("avatar") String avatar);

    /**
     * App端更新用户信息
     *
     * @param phone    手机号（可选）
     * @param password 密码（可选）
     * @param name     用户名（可选）
     * @param avatar   头像URL（可选）
     * @return 更新结果
     */
    @RequestMapping("/update")
    boolean updateUser(
                    @RequestParam(name = "userId") BigInteger userId,
                    @RequestParam(value = "phone", required = false) String phone,
                    @RequestParam(value = "password", required = false) String password,
                    @RequestParam(value = "name", required = false) String name,
                    @RequestParam(value = "avatar", required = false) String avatar);

    /**
     * App端获取当前登录用户信息
     * 认证信息通过FeignRequestInterceptor自动传递
     *
     * @return 用户信息
     */
    @RequestMapping("/info")
    UserInfoVO getUserInfo(
            @RequestParam("userId") BigInteger userId
    );
}