package com.example.common.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果封装类
 * 支持Jackson序列化和反序列化
 * @param <T> 响应数据类型
 */
@Getter
@NoArgsConstructor // Jackson反序列化需要无参构造函数
public class Response<T> {
    private ResponseStatus status = new ResponseStatus();
    private T result;

    /**
     * 构造响应对象（仅状态码）
     * @param statusCode 状态码
     */
    public Response(int statusCode) {
        this.status.setCode(statusCode);
        this.status.setMsg(ResponseCode.getMsg(statusCode));
        this.result = null;
    }

    /**
     * 构造响应对象（状态码和结果数据）
     * @param statusCode 状态码
     * @param result 响应数据
     */
    public Response(int statusCode, T result) {
        this.status.setCode(statusCode);
        this.status.setMsg(ResponseCode.getMsg(statusCode));
        this.result = result;
    }
    
    /**
     * 构造响应对象（状态码和自定义消息）
     * @param statusCode 状态码
     * @param customMsg 自定义消息
     */
    public Response(int statusCode, String customMsg) {
        this.status.setCode(statusCode);
        this.status.setMsg(customMsg);
        this.result = null;
    }

    /**
     * Jackson反序列化构造函数
     * @param status 状态对象
     * @param result 结果数据
     */
    @JsonCreator
    public Response(@JsonProperty("status") ResponseStatus status, 
                   @JsonProperty("result") T result) {
        this.status = status != null ? status : new ResponseStatus();
        this.result = result;
    }
}