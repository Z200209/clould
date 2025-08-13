package com.example.consumer.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Consumer端Web MVC配置
 * 配置认证拦截器和参数解析器，统一处理认证逻辑
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final ApplicationArguments appArguments;

    public WebMvcConfiguration(ApplicationArguments appArguments) {
        this.appArguments = appArguments;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(newUserAuthResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(newBaseInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/console/user/login", "/console/user/register");
    }

    @Bean
    public UserAuthorityResolver newUserAuthResolver() {
        return new UserAuthorityResolver(appArguments);
    }

    @Bean
    public BaseInterceptor newBaseInterceptor() {
        return new BaseInterceptor();
    }
}