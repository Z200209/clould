package com.example.consumerapp.controller.domain.user;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

/**
 * 用户信息数据传输对象
 */
@Data
@Accessors(chain = true)
public class UserInfoVO {
    private BigInteger id;
    private String phone;
    private String name;
    private String avatar;
} 