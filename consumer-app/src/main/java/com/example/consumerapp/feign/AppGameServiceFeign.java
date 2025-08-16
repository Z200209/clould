package com.example.consumerapp.feign;

import com.example.common.entity.Game;
import com.example.common.entity.Tag;
import com.example.common.entity.Type;
import com.example.consumerapp.controller.domain.game.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/**
 * 游戏服务Feign客户端接口
 */
@FeignClient(name = "provider", contextId = "consoleGameServiceFeign", path = "/game")
public interface AppGameServiceFeign {
    /**
     * 获取游戏信息
     *
     * @param gameId
     * @return
     */
    @RequestMapping("/info")
    Game gameInfo(
            @RequestParam(name = "gameId") BigInteger gameId);

    /**
     * 获取游戏列表
     */
    @RequestMapping("/list")
    List<Game> gameList(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "typeId", required = false) BigInteger type);

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
     * 删除游戏
     *
     * @param gameId
     * @return
     */
    @RequestMapping("/delete")
    boolean deleteGame(
            @RequestParam(name = "gameId") BigInteger gameId);

    /**
     * 获取游戏总数
     *
     * @param keyword
     * @return
     */
    @RequestMapping("/totalCount")
    Integer getTotalCount(
            @RequestParam(name = "keyword", required = false) String keyword);

    /**
     * 获取类型列表
     *
     * @param keyword
     * @return
     */
    @RequestMapping("/type/list")
    List<Type> typeList(
            @RequestParam(name = "keyword", required = false) String keyword);

    /**
     * 获取类型信息
     *
     * @param typeId
     * @return
     */
    @RequestMapping("/type/info")
    Type typeInfo(
            @RequestParam(name = "typeId") BigInteger typeId);

    /**
     * 获取类型子类型列表
     *
     * @param typeId
     * @return
     */
    @RequestMapping("/type/childrenList")
    List<ChildrenVO> childrenList(
            @RequestParam(name = "typeId") BigInteger typeId);


    /**
     * 创建类型
     *
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
     *
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
     *
     * @param typeId
     * @return
     */
    @RequestMapping("/type/delete")
    boolean deleteType(
            @RequestParam(name = "typeId") BigInteger typeId);

    /**
     * 获取类型树
     *
     * @return
     */
    @RequestMapping("/type/rootTree")
    List<Type> rootTypes();

    /**
     * 根据类型id列表批量获取类型
     *
     * @param typeIds
     * @return
     */
    @RequestMapping("/type/listByIds")
    List<Type> typeListByIds(
            @RequestParam(name = "typeIds") Set<BigInteger> typeIds);
    /**
     * 根据游戏id获取标签列表
     *
     * @param gameId
     * @return
     */
    @RequestMapping("/getTagsByGameId")
    List<Tag> getTagsByGameId(
            @RequestParam(name = "gameId") BigInteger gameId);

    /**
     * 从缓存获取游戏列表
     *
     * @param keyword
     * @param typeId
     * @param page
     * @return
     */
    @RequestMapping("/listFromRedis")
    Object listFromRedis(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "typeId", required = false) BigInteger typeId,
            @RequestParam(name = "currentPage",required = false) Integer page);

    /**
     * 将游戏列表存入缓存
     *
     * @param cacheKey
     * @param result
     * @return
     */
    @RequestMapping("/listIntoRedis")
    void listIntoRedis(
            @RequestParam(name = "cacheKey", required = false) String cacheKey,
            @RequestParam(name = "result", required = false) Object result);

}



