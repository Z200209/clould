package com.example.consumer.controller.domain.sms;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

/**
 * 短信记录数据传输对象
 */
@Data
@Accessors(chain = true)
public class SmsVO {
    private BigInteger id;
    private String phone;
    private String content;
    private String templateCode;
    private String templateParam;
    private Integer status; // 0-发送中 1-成功 2-失败
    private String bizId;
    private Integer createTime;
    private Integer updateTime;
    private Integer sendTime;
}