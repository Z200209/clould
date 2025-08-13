package com.example.consumerapp.controller;

import com.example.common.annotations.VerifiedUser;
import com.example.common.entity.User;
import com.example.common.utils.Response;

import com.example.consumerapp.feign.AppGameServiceFeign;
import com.example.provider.controller.domain.game.ChildrenListVO;
import com.example.provider.controller.domain.game.GameInfoVO;
import com.example.provider.controller.domain.game.GameListVO;
import com.example.provider.controller.domain.game.TypeVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/app/game")
public class AppGameController {
    @Resource
    private AppGameServiceFeign appGameServiceFeign;

    @RequestMapping("/info")
    public Response getAppGameInfo(@VerifiedUser User loginUser,
                                   @RequestParam(name = "gameId") BigInteger gameId) {
        if (loginUser == null) {
            return new Response<>(1002);
        }
        try {
           GameInfoVO  gameInfo = appGameServiceFeign.getAppGameInfo(gameId);
           if (gameInfo == null) {
               return new Response<>(4004);
           }
           return new Response<>(1001, gameInfo);
        } catch (Exception e) {
            log.error("获取游戏信息失败: {}", e.getMessage(), e);
            return new Response<>(4004);
        }

    }

    @RequestMapping("/list")
    public Response getAppGameList(@VerifiedUser User loginUser,
                                   @RequestParam(name = "keyword", required = false) String keyword,
                                   @RequestParam(name = "typeId", required = false) BigInteger typeId,
                                   @RequestParam(name = "wp", required = false) String wp) {
        if (loginUser == null) {
            return new Response<>(1002);
        }
        try {
            GameListVO gameList = appGameServiceFeign.getAppGameList(keyword, typeId, wp);
            if (gameList == null) {
                return new Response<>(4004);
            }
            return new Response<>(1001, gameList);
        } catch (Exception e) {
            log.error("获取游戏列表失败: {}", e.getMessage(), e);
            return new Response(4004);
        }

    }

    @RequestMapping("/type/list")
    public Response<List<TypeVO>> getAppGameTypeList(@VerifiedUser User loginUser,
                                       @RequestParam(name = "keyword", required = false) String keyword) {
        if (loginUser == null) {
            return new Response<>(1002);
        }
        try {
            List<TypeVO> typeList = appGameServiceFeign.getAppGameTypeList(keyword);
            if (typeList == null) {
                return new Response<>(4004);
            }
            return new Response<>(1001, typeList);
        } catch (Exception e) {
            log.error("获取游戏类型列表失败: {}", e.getMessage(), e);
            return new Response(4004);
        }
    }

    @RequestMapping("/type/childrenList")
    public Response getAppGameTypeChildren(@VerifiedUser User loginUser,
                                          @RequestParam(name = "typeId") BigInteger typeId) {
        if (loginUser == null) {
            return new Response<>(1002);
        }

        try {
            ChildrenListVO childrenList = appGameServiceFeign.getAppGameTypeChildren(loginUser, typeId);
            if (childrenList == null) {
                return new Response<>(4004);
            }
            return new Response(1001, childrenList);
        } catch (Exception e) {
            log.error("获取游戏类型子类型列表失败: {}", e.getMessage(), e);
            return new Response(4004);
        }
    }
}
