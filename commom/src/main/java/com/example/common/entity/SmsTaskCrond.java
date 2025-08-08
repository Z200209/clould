package com.example.common.entity;

import lombok.Data;

import java.math.BigInteger;

@Data
public class SmsTaskCrond {
    private BigInteger id;
    private String phone;  // 手机号
    private String templateParam;  // 模板参数
    private Integer status; // 任务状态：0-初始 1-完成 2-失败
    private Integer executeTime;   // 实际执行时间戳
    private Integer createTime;    // 创建时间戳
    private Integer updateTime;    // 更新时间戳
    private Integer isDeleted;  // 逻辑删除标记：0-未删除 1-已删除
}