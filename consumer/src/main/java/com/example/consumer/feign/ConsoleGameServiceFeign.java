package com.example.consumer.feign;

import com.example.common.annotations.VerifiedUser;
import com.example.common.dto.TypeListVO;
import com.example.common.entity.User;
import com.example.common.utils.Response;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;
import java.util.List;

/**
 * 游戏服务Feign客户端接口
 * 用于调用game-service中的游戏相关服务
 */
@FeignClient(name = "provider", contextId = "consoleGameServiceFeign", path = "/console/game")
public interface ConsoleGameServiceFeign {
    /**
     * 创建游戏
     * @param typeId
     * @param gameName
     * @param price
     * @param gameIntroduction
     * @param gameDate
     * @param gamePublisher
     * @param images
     * @param tags
     * @return
     */
    @RequestMapping("/create")
    Response createGame(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeId", required = false) BigInteger typeId,
            @RequestParam(name = "gameName") String gameName,
            @RequestParam(name = "price") Float price,
            @RequestParam(name = "gameIntroduction") String gameIntroduction,
            @RequestParam(name = "gameDate") String gameDate,
            @RequestParam(name = "gamePublisher") String gamePublisher,
            @RequestParam(name = "images") String images,
            @RequestParam(name = "tags") String tags);

    /**
     * 更新游戏
     * @param gameId
     * @param typeId
     * @param gameName
     * @param price
     * @param gameIntroduction
     * @param gameDate
     * @param gamePublisher
     * @param images
     * @param tags
     * @return
     */
    @RequestMapping("/update")
    Response updateGame(
            @VerifiedUser User loginUser,
            @RequestParam(name = "gameId", required = false) BigInteger gameId,
            @RequestParam(name = "typeId", required = false) BigInteger typeId,
            @RequestParam(name = "gameName", required = false) String gameName,
            @RequestParam(name = "price", required = false) Float price,
            @RequestParam(name = "gameIntroduction", required = false) String gameIntroduction,
            @RequestParam(name = "gameDate", required = false) String gameDate,
            @RequestParam(name = "gamePublisher", required = false) String gamePublisher,
            @RequestParam(name = "images", required = false) String images,
            @RequestParam(name = "tags", required = false) String tags

    );

    /**
     * 获取游戏列表
     * @param page
     * @param keyword
     * @param typeId
     * @return
     */
    @RequestMapping("/list")
    Response gameList(
            @VerifiedUser User loginUser,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "typeId", required = false) BigInteger typeId);

    /**
     * 获取游戏信息
     * @param gameId
     * @return
     */
    @RequestMapping("/info")
    Response gameInfo(
            User loginUser,
            @RequestParam(name = "gameId") BigInteger gameId);

    /**
     * 删除游戏
     * @param gameId
     * @return
     */
    @RequestMapping("/delete")
    Response deleteGame(
            @VerifiedUser User loginUser,
            @RequestParam(name = "gameId") BigInteger gameId);

    /**
     * 获取类型列表
     * @param keyword
     * @return
     */
    @RequestMapping("/type/list")
    Response<List<TypeListVO>> typeList(@VerifiedUser User loginUser,
                                        @RequestParam(name = "keyword", required=false) String keyword);

    /**
     * 获取类型信息
     * @param typeId
     * @return
     */
    @RequestMapping("/type/info")
    Response typeInfo(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeId") BigInteger typeId);

    /**
     * 创建类型
     * @param typeName
     * @param image
     * @param parentId
     * @return
     */
    @RequestMapping("/type/create")
    Response createType(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeName") String typeName,
            @RequestParam(name = "image") String image,
            @RequestParam(name = "parentId", required = false) BigInteger parentId);

    /**
     * 更新类型
     * @param typeId
     * @param typeName
     * @param image
     * @param parentId
     * @return
     */
    @RequestMapping("/type/update")
    Response updateType(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeId") BigInteger typeId,
            @RequestParam(name = "typeName") String typeName,
            @RequestParam(name = "image") String image,
            @RequestParam(name = "parentId", required = false) BigInteger parentId);

    /**
     * 删除类型
     * @param typeId
     * @return
     */
    @RequestMapping("/type/delete")
    Response deleteType(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeId") BigInteger typeId);

    /**
     * 获取类型树
     * @param keyword
     * @return
     */
    @RequestMapping("/type/tree")
    Response typeTree(
            @VerifiedUser User loginUser,
            @RequestParam(name = "keyword", required = false) String keyword);




}