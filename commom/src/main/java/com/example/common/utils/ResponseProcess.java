package com.example.common.utils;

/**
 * 响应处理工具类
 * 简化版本：判定成功后检查消息是否为空并返回消息
 */
public class ResponseProcess {

    /**
     * 检查响应是否成功
     * @param response 响应对象
     * @return 是否成功（状态码为1001）
     */
    public static <T> boolean isSuccess(Response<T> response) {
        return response != null && response.getStatus() != null && response.getStatus().getCode() == 1001;
    }

    /**
     * 获取响应数据（如果成功）
     * @param response 响应对象
     * @return 响应数据，如果失败则返回null
     */
    public static <T> T getData(Response<T> response) {
        if (isSuccess(response)) {
            return response.getResult();
        }
        return null;
    }

    /**
     * 获取响应消息（如果成功且消息不为空）
     * @param response 响应对象
     * @return 响应消息，如果失败或消息为空则返回null
     */
    public static <T> String getMessageIfSuccess(Response<T> response) {
        if (isSuccess(response)) {
            String message = response.getStatus().getMsg();
            return (message != null && !message.trim().isEmpty()) ? message : null;
        }
        return null;
    }
}
