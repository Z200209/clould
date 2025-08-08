package com.example.common.utils;

import java.util.HashMap;
import java.util.Map;

public class ResponseCode {
    private static final Map<Integer, String> statusMap = new HashMap<Integer, String>();

    static {
        statusMap.put(1001, "成功");
        statusMap.put(1002, "没有登录");
        statusMap.put(1010, "账号密码不匹配或账号不存在");
        statusMap.put(1011, "账号已被禁用");

        statusMap.put(2014,"账号尚未注册");
        statusMap.put(2015,"账号已存在");

        statusMap.put(4003,"没有权限");
        statusMap.put(4004,"链接超时");
        statusMap.put(4005,"请求参数错误");
        statusMap.put(4006,"请求资源不存在");
        statusMap.put(4007,"请求参数为空");
        statusMap.put(4008,"读取图片尺寸失败");

    }

    public static String getMsg(Integer code) {
        return statusMap.get(code);

    }
}
