package com.example.common.entity;

import lombok.Data;

import java.math.BigInteger;

@Data
public class Sms {
    private BigInteger id;
    private String phone;
    private String content;  // 内容
    private String templateCode;  // 模板编号
    private String templateParam;  // 模板参数
    private Integer status; // 0-发送中 1-成功 2-失败
    private String bizId;       // 阿里云返回的业务ID
    private Integer createTime;    // 创建时间戳
    private Integer updateTime;    // 更新时间戳
    private Integer sendTime;      // 发送时间戳
    private Integer isDeleted;  // 逻辑删除标记：0-未删除 1-已删除
}
