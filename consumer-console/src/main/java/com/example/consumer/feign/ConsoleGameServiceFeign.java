package com.example.consumer.feign;


import com.example.common.dto.TypeListVO;
import com.example.provider.controller.domain.game.DetailVO;
import com.example.provider.controller.domain.game.TypeDetailVO;
import com.example.provider.controller.domain.game.TypeTreeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 游戏服务Feign客户端接口
 */
@FeignClient(name = "provider", contextId = "consoleGameServiceFeign", path = "/console/game")
public interface ConsoleGameServiceFeign {
    /**
     * 创建游戏
     */
    @RequestMapping("/create")
    BigInteger createGame(
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
     */
    @RequestMapping("/update")
    boolean updateGame(
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
     */
    @RequestMapping("/list")
    Map<String, Object> gameList(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "typeId", required = false) BigInteger typeId);

    /**
     * 获取游戏信息
     * @param gameId
     * @return
     */
    @RequestMapping("/info")
    DetailVO gameInfo(
            @RequestParam(name = "gameId") BigInteger gameId);

    /**
     * 删除游戏
     * @param gameId
     * @return
     */
    @RequestMapping("/delete")
    boolean deleteGame(
            @RequestParam(name = "gameId") BigInteger gameId);

    /**
     * 获取类型列表
     * @param keyword
     * @return
     */
    @RequestMapping("/type/list")
    List<TypeListVO> typeList(
            @RequestParam(name = "keyword", required=false) String keyword);

    /**
     * 获取类型信息
     * @param typeId
     * @return
     */
    @RequestMapping("/type/info")
    TypeDetailVO typeInfo(
            @RequestParam(name = "typeId") BigInteger typeId);

    /**
     * 创建类型
     * @param typeName
     * @param image
     * @param parentId
     * @return
     */
    @RequestMapping("/type/create")
    BigInteger createType(
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
    boolean updateType(
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
    boolean deleteType(
            @RequestParam(name = "typeId") BigInteger typeId);

    /**
     * 获取类型树
     * @param keyword
     * @return
     */
    @RequestMapping("/type/tree")
    List<TypeTreeVO> typeTree(
            @RequestParam(name = "keyword", required = false) String keyword);

}