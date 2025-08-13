package com.example.consumer.controller;

import com.example.common.annotations.VerifiedUser;
import com.example.common.dto.TypeListVO;
import com.example.common.entity.User;
import com.example.common.utils.Response;
import com.example.consumer.feign.ConsoleGameServiceFeign;
import com.example.provider.controller.domain.game.DetailVO;
import com.example.provider.controller.domain.game.TypeDetailVO;
import com.example.provider.controller.domain.game.TypeTreeVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/console/game")
public class ConsoleGameController {
    @Resource
    private ConsoleGameServiceFeign consoleGameServiceFeign;

    @RequestMapping("/create")
    public Response createGame(@VerifiedUser User loginUser,
                               @RequestParam(name = "typeId", required = false) BigInteger typeId,
                               @RequestParam(name = "gameName") String gameName,
                               @RequestParam(name = "price") Float price,
                               @RequestParam(name = "gameIntroduction") String gameIntroduction,
                               @RequestParam(name = "gameDate") String gameDate,
                               @RequestParam(name = "gamePublisher") String gamePublisher,
                               @RequestParam(name = "images") String images,
                               @RequestParam(name = "tags") String tags
                               ) {
        if(loginUser == null){
            return new Response(1002, "用户未登录");
        }
        try {
        BigInteger gameId = consoleGameServiceFeign.createGame( typeId, gameName, price, gameIntroduction, gameDate, gamePublisher, images, tags);
        if(gameId == null){
            return new Response(4004, "创建失败");
        }
        return new Response(1001, gameId);
    } catch (Exception e) {
            log.error("创建游戏失败", e);
            return new Response(4004, "创建失败");
        }
    }

    @RequestMapping("/update")
    public Response updateGame(
            @VerifiedUser User loginUser,
            @RequestParam(name = "gameId", required = false) BigInteger gameId,
            @RequestParam(name = "typeId", required = false) BigInteger typeId,
            @RequestParam(name = "gameName", required = false) String gameName,
            @RequestParam(name = "price", required = false) Float price,
            @RequestParam(name = "gameIntroduction", required = false) String gameIntroduction,
            @RequestParam(name = "gameDate", required = false) String gameDate,
            @RequestParam(name = "gamePublisher", required = false) String gamePublisher,
            @RequestParam(name = "images", required = false) String images,
            @RequestParam(name = "tags", required = false) String tags){
        if(loginUser == null){
            return new Response(1002, "用户未登录");
        }
        try {
        boolean result = consoleGameServiceFeign.updateGame(gameId, typeId, gameName, price, gameIntroduction, gameDate, gamePublisher, images, tags);
        return result ? new Response(1001) : new Response(4004, "更新失败");
    } catch (Exception e) {
            log.error("更新游戏失败", e);
            return new Response(4004, "更新失败");
        }
    }

    @RequestMapping("/list")
    public Response gameList(@VerifiedUser User loginUser,
                            @RequestParam(name = "page", defaultValue = "1") Integer page,
                            @RequestParam(name = "keyword", required = false) String keyword,
                            @RequestParam(name = "typeId", required = false) BigInteger typeId){
        if(loginUser == null){
            return new Response(1002, "用户未登录");
        }
        try {
            Map<String, Object>  gameList =consoleGameServiceFeign.gameList(page, keyword, typeId);
            if (gameList == null){
                return new Response(4004, "游戏列表数据为空");
            }
            return new Response(1001, gameList);
        } catch (Exception e) {
            log.error("获取游戏列表失败", e);
            return new Response(4004, "获取游戏列表失败");
        }
    }

    @RequestMapping("/info")
    public Response gameInfo(@VerifiedUser User loginUser,
                           @RequestParam(name = "gameId") BigInteger gameId){
        if(loginUser == null){
            return new Response(1002, "用户未登录");
        }
        try {
            DetailVO gameInfo = consoleGameServiceFeign.gameInfo( gameId);
            if (gameInfo == null){
                return new Response(4004, "游戏信息为空");
            }
            return new Response(1001, gameInfo);
        } catch (Exception e) {
            log.error("获取游戏信息失败", e);
            return new Response(4004, "获取游戏信息失败");
        }
    }

    @RequestMapping("/delete")
    public Response deleteGame(@VerifiedUser User loginUser,
                                @RequestParam(name = "gameId") BigInteger gameId) {
        if(loginUser == null){
            return new Response(1002, "用户未登录");
        }
        try {
            boolean result = consoleGameServiceFeign.deleteGame(gameId);
            return result ? new Response(1001) : new Response(4004, "删除失败");
        } catch (Exception e) {
            log.error("删除游戏失败", e);
            return new Response(4004, "删除失败");
        }
    }

    @RequestMapping("/type/list")
    public Response typeList(@VerifiedUser User loginUser,
                            @RequestParam(name = "keyword", required=false) String keyword){
        if(loginUser == null){
            return new Response(1002, "用户未登录");
        }
        try {
            List<TypeListVO> typeList = consoleGameServiceFeign.typeList(keyword);
            if (typeList == null){
                return new Response(4004, "游戏类型列表数据为空");
            }
            return new Response(1001, typeList);
        } catch (Exception e) {
            log.error("获取游戏类型列表失败", e);
            return new Response(4004, "获取游戏类型列表失败");
        }
    }

    @RequestMapping("/type/info")
    public Response typeInfo(@VerifiedUser User loginUser,
                             @RequestParam(name = "typeId") BigInteger typeId){
        if(loginUser == null){
            return new Response(1002, "用户未登录");
        }
        try {
            TypeDetailVO typeInfo = consoleGameServiceFeign.typeInfo(typeId);
            if (typeInfo == null){
                return new Response(4004, "游戏类型信息为空");
            }
            return new Response(1001, typeInfo);
        } catch (Exception e) {
            log.error("获取游戏类型信息失败", e);
            return new Response(4004, "获取游戏类型信息失败");
        }
    }


    @RequestMapping("/type/create")
    public Response createType(@VerifiedUser User loginUser,
                             @RequestParam(name = "typeName") String typeName,
                             @RequestParam(name = "image") String image,
                             @RequestParam(name = "parentId", required = false) BigInteger parentId){
    if(loginUser == null){
            return new Response(1002, "用户未登录");
        }
        try {
            BigInteger typeId = consoleGameServiceFeign.createType(typeName, image, parentId);
            if (typeId == null){
                return new Response(4004, "创建游戏类型失败");
            }
            return new Response(1001, typeId);
        }catch (Exception e){
            log.error("创建游戏类型失败", e);
            return new Response(4004, "创建游戏类型失败");
        }
    }

    @RequestMapping("/type/update")
    public Response updateType(@VerifiedUser User loginUser,
                               @RequestParam(name = "typeId") BigInteger typeId,
                               @RequestParam(name = "typeName") String typeName,
                               @RequestParam(name = "image") String image,
                               @RequestParam(name = "parentId", required = false) BigInteger parentId){
        if(loginUser == null){
            return new Response(1002, "用户未登录");
        }
        try {
            boolean result = consoleGameServiceFeign.updateType(typeId, typeName, image, parentId);
            return result ? new Response(1001) : new Response(4004, "更新失败");
        } catch (Exception e) {
            log.error("更新游戏类型失败", e);
            return new Response(4004, "更新失败");
        }
    }

    @RequestMapping("/type/delete")
    public Response deleteType(@VerifiedUser User loginUser,
                             @RequestParam(name = "typeId") BigInteger typeId){
        if(loginUser == null){
            return new Response(1002, "用户未登录");
        }
        try {
            boolean result = consoleGameServiceFeign.deleteType(typeId);
            return result ? new Response(1001) : new Response(4004, "删除失败");
        }catch (Exception e){
            log.error("删除游戏类型失败", e);
            return new Response(4004, "删除失败");
        }
    }

    @RequestMapping("/type/tree")
    public Response typeTree(@VerifiedUser User loginUser,
                             @RequestParam(name = "keyword", required = false) String keyword){
        if(loginUser == null){
            return new Response(1002, "用户未登录");
        }
        try {
            List<TypeTreeVO> typeTreeList = consoleGameServiceFeign.typeTree(keyword);
           return typeTreeList == null ? new Response(4004, "游戏类型树为空") : new Response(1001, typeTreeList);

        } catch (Exception e) {
            log.error("获取游戏类型树失败", e);
            return new Response(4004, "获取游戏类型树失败");
        }
    }
}
