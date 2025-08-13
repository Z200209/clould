package com.example.consumerapp.feign;

import com.example.common.entity.Sign;
import com.example.common.entity.User;
import com.example.common.utils.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;

/**
 * Consumer端认证服务Feign接口
 * 用于调用Provider端的认证相关服务
 */
@FeignClient(value = "provider", path = "/auth")
public interface AuthServiceFeign {

    /**
     * 解析签名
     * 调用Provider端的签名解析服务
     *
     * @param sign 签名字符串
     * @return 解析后的签名对象
     */
    @RequestMapping("/analyzeSign")
    Response<Sign> analyzeSign(@RequestParam("sign") String sign);

    /**
     * 获取用户信息
     * 调用Provider端的用户查询服务
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @RequestMapping("/getUser")
    Response<User> getUser(@RequestParam("userId") BigInteger userId);

    /**
     * 用户登录
     * 调用Provider端的登录服务
     *
     * @param phone 手机号
     * @param password 密码
     * @return 登录结果，成功时返回token
     */
    @RequestMapping("/login")
    Response<String> login(@RequestParam("phone") String phone,
                          @RequestParam("password") String password);
}