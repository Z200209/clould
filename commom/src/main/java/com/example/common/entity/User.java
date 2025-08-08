package com.example.common.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;
@Data
@Accessors(chain = true)
public class User {
    private BigInteger id;
    private String phone;
    private String password;
    private String name;
    private String avatar;
    private Integer createTime;
    private Integer updateTime;

}
