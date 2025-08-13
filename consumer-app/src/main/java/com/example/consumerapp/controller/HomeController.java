package com.example.consumerapp.controller;
import com.example.common.annotations.VerifiedUser;
import com.example.common.entity.User;
import com.example.common.utils.Response;
import com.example.consumerapp.service.HomeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 首页控制器 - 提供复杂的首页接口，使用多线程优化性能
 */
@Slf4j
@RestController
@RequestMapping("/app/home")
public class HomeController {

    @Autowired
    private HomeService homeService;

    /**
     * 获取首页所有数据
     * 这是一个复杂的首页接口，包含：
     * 1. banner（image、link）
     * 2. channel（name、icon、id）
     * 3. event（title、image、id）
     * 4. 推荐内容（商品列表）（title、price、image、id）
     */
    @RequestMapping("/index")
    public Response getHomePage(@VerifiedUser User loginUser) {
        long startTime = System.currentTimeMillis();
        try {
            // 使用多线程并发获取首页各模块数据
            Map<String, Object> homeData = homeService.getHomePageData();

            long endTime = System.currentTimeMillis();
            log.info("首页数据获取成功，耗时: {}ms", endTime - startTime);

            return new Response(1001,  homeData);

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("获取首页数据失败，耗时: {}ms", endTime - startTime, e);
            return new Response(4004);
        }
    }

    /**
     * 获取单个模块数据 - 用于局部刷新
     * 支持的模块类型：banner、channel、event、product
     */
    @RequestMapping("/module")
    public Response getModuleData(User loginUser,
                                  @RequestParam("type") String moduleType) {
        long startTime = System.currentTimeMillis();
        log.info("用户请求模块数据，模块类型: {}, 用户ID: {}",
                moduleType, loginUser != null ? loginUser.getId() : "游客");

        try {
            Object moduleData = homeService.getModuleData(moduleType);

            long endTime = System.currentTimeMillis();
            log.info("模块数据获取成功，模块类型: {}, 耗时: {}ms", moduleType, endTime - startTime);

            return new Response(1001, moduleData);

        } catch (IllegalArgumentException e) {
            long endTime = System.currentTimeMillis();
            log.warn("不支持的模块类型: {}, 耗时: {}ms", moduleType, endTime - startTime);
            return new Response(4004, e.getMessage());

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("获取模块数据失败，模块类型: {}, 耗时: {}ms", moduleType, endTime - startTime, e);
            return new Response(4004, "获取模块数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取Banner数据
     */
    @RequestMapping("/banner")
    public Response getBanners(User loginUser) {
        log.info("用户请求Banner数据，用户ID: {}", loginUser != null ? loginUser.getId() : "游客");

        try {
            Object banners = homeService.getModuleData("banner");
            return new Response(1001, banners);
        } catch (Exception e) {
            log.error("获取Banner数据失败", e);
            return new Response(4004, "获取Banner数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取频道数据
     */
    @RequestMapping("/channel")
    public Response getChannels(User loginUser) {
        log.info("用户请求频道数据，用户ID: {}", loginUser != null ? loginUser.getId() : "游客");

        try {
            Object channels = homeService.getModuleData("channel");
            return new Response(1001,channels);
        } catch (Exception e) {
            log.error("获取频道数据失败", e);
            return new Response(4004, "获取频道数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取活动数据
     */
    @RequestMapping("/event")
    public Response getEvents(User loginUser) {
        log.info("用户请求活动数据，用户ID: {}", loginUser != null ? loginUser.getId() : "游客");

        try {
            Object events = homeService.getModuleData("event");
            return new Response(1001, events);
        } catch (Exception e) {
            log.error("获取活动数据失败", e);
            return new Response(4004, "获取活动数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取推荐商品数据
     */
    @RequestMapping("/recommend")
    public Response getRecommendProducts(User loginUser) {
        log.info("用户请求推荐商品数据，用户ID: {}", loginUser != null ? loginUser.getId() : "游客");

        try {
            Object products = homeService.getModuleData("product");
            return new Response(1001,products);
        } catch (Exception e) {
            log.error("获取推荐商品数据失败", e);
            return new Response(4004, "获取推荐商品数据失败: " + e.getMessage());
        }
    }
}