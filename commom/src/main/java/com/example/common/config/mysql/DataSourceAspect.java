package com.example.common.config.mysql;


import com.example.common.annotations.DataSource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 数据源切面
 * 用于拦截@DataSource注解，自动切换数据源
 */
@Aspect   // @Aspect  切面
@Component // @Component  组件 创建对象
@Order(1) // 确保在事务切面之前执行
public class DataSourceAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSourceAspect.class);
    
    /**
     * 切点：拦截所有带有@DataSource注解的方法
     */
    @Pointcut("@annotation(com.example.common.annotations.DataSource)")
    public void dataSourcePointcut() {}
    
    /**
     * 环绕通知：在方法执行前后切换数据源
     */
    @Around("dataSourcePointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        
        // 获取@DataSource注解
        DataSource dataSource = method.getAnnotation(DataSource.class);
        if (dataSource != null) {
            DataSourceType dataSourceType = dataSource.value();
            logger.debug("切换到数据源: {}", dataSourceType);
            DataSourceContextHolder.setDataSourceType(dataSourceType);
        }
        
        try {
            return point.proceed();
        } finally {
            // 清除数据源设置
            DataSourceContextHolder.clearDataSourceType();
            logger.debug("清除数据源设置");
        }
    }
}