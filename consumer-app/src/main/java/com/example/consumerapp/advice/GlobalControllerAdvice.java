package com.example.consumerapp.advice;


import com.example.common.utils.Response;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalControllerAdvice {   // 全局异常处理
    @ExceptionHandler(Exception.class)
    @ResponseBody
    Response handleControllerException(Exception exception) {
        return new Response(4004, exception.getStackTrace());
    }

}




