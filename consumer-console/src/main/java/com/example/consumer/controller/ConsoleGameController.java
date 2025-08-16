package com.example.consumer.controller;

import com.alibaba.fastjson.JSON;
import com.example.common.annotations.VerifiedUser;
import com.example.common.dto.TypeListVO;
import com.example.common.entity.Game;
import com.example.common.entity.Type;
import com.example.common.entity.User;
import com.example.common.utils.BaseUtils;
import com.example.common.utils.Response;
import com.example.consumer.controller.domain.game.*;
import com.example.consumer.feign.ConsoleGameServiceFeign;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/console/game")
public class ConsoleGameController {
    @Resource
    private ConsoleGameServiceFeign consoleGameServiceFeign;


    @RequestMapping("/list")
    public Response gameList(@VerifiedUser User loginUser,
                             @RequestParam(name = "page", defaultValue = "1") Integer page,
                             @RequestParam(name = "keyword", required = false) String keyword,
                             @RequestParam(name = "typeId", required = false) BigInteger typeId) {
        if (loginUser == null) {
            return new Response(1002, "用户未登录");
        }
        int pageSize = 10;
        List<Game> gameList;
        try {
            gameList = consoleGameServiceFeign.gameList(page, keyword, typeId);

            if (gameList == null) {
                return new Response(4004, "游戏列表数据为空");
            }
        } catch (Exception e) {
            log.error("获取游戏列表失败", e);
            return new Response(4004, "获取游戏列表失败");
        }
        Integer total = consoleGameServiceFeign.getTotalCount(keyword);

        Set<BigInteger> gameIdSet = new HashSet<>();
        Set<BigInteger> typeIdSet = new HashSet<>();
        for (Game game : gameList) {
            gameIdSet.add(game.getId());
            BigInteger tid = game.getTypeId();
            if (tid != null) {
                typeIdSet.add(tid);
            }
        }
        Map<BigInteger, String> typeMap = new HashMap<>();
        if (!typeIdSet.isEmpty()) {
            try {
                List<Type> types = consoleGameServiceFeign.typeListByIds(typeIdSet);
                for (Type type : types) {
                    typeMap.put(type.getId(), type.getTypeName());
                }
            } catch (Exception e) {
                log.info("获取类型信息失败: {}", e.getMessage(), e);
            }
        }
        List<GameListVO> gameVOList = new ArrayList<>();
        for (Game game : gameList) {
            String typeName = typeMap.get(game.getTypeId());
            String formattedCreateTime = BaseUtils.timeStamp2DateGMT(game.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
            String formattedUpdateTime = BaseUtils.timeStamp2DateGMT(game.getUpdateTime(), "yyyy-MM-dd HH:mm:ss");
            GameListVO gameVO = new GameListVO()
                    .setGameId(game.getId())
                    .setGameName(game.getGameName())
                    .setTypeName(typeName)
                    .setPrice(game.getPrice())
                    .setCreateTime(formattedCreateTime)
                    .setUpdateTime(formattedUpdateTime);

            gameVOList.add(gameVO);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("list", gameVOList);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);

        return new Response<>(1001, result);

    }

    @RequestMapping("/info")
    public Response gameInfo(@VerifiedUser User loginUser,
                             @RequestParam(name = "gameId") BigInteger gameId) {
        if (loginUser == null) {
            return new Response(1002, "用户未登录");
        }

        try {
            Game game = consoleGameServiceFeign.gameInfo(gameId);

            DetailVO gameInfo = new DetailVO();
            gameInfo.setGameId(game.getId())
                    .setGameName(game.getGameName())
                    .setPrice(game.getPrice())
                    .setGameDate(game.getGameDate())
                    .setGamePublisher(game.getGamePublisher())
                    .setCreateTime(BaseUtils.timeStamp2Date(game.getCreateTime()))
                    .setUpdateTime(BaseUtils.timeStamp2Date(game.getUpdateTime()));
            try {
                List<BaseIntroductionVO> introductionList = JSON.parseArray(game.getGameIntroduction(), BaseIntroductionVO.class);
                gameInfo.setGameIntroduction(introductionList);
            } catch (Exception e) {
                log.error("解析游戏介绍失败: {}", e.getMessage(), e);
                return new Response(4004);
            }

            if (game.getImages() != null && !game.getImages().isEmpty()) {
                gameInfo.setImages(Arrays.asList(game.getImages().split("\\$")));
            }
            if (gameInfo == null) {
                return new Response(4004, "游戏信息为空");
            }
            return new Response(1001, gameInfo);
        } catch (Exception e) {
            log.error("获取游戏信息失败", e);
            return new Response(4004, "获取游戏信息失败");
        }
    }

    @RequestMapping("/create")
    public Response<?> createGame(@VerifiedUser User loginUser,
                                  @RequestParam(name = "typeId", required = false) BigInteger typeId,
                                  @RequestParam(name = "gameName") String gameName,
                                  @RequestParam(name = "price") Float price,
                                  @RequestParam(name = "gameIntroduction") String gameIntroduction,
                                  @RequestParam(name = "gameDate") String gameDate,
                                  @RequestParam(name = "gamePublisher") String gamePublisher,
                                  @RequestParam(name = "images") String images,
                                  @RequestParam(name = "tags") String tags
    ) {
        if (loginUser == null) {
            return new Response<>(1002, "用户未登录");
        }
        try {
            BigInteger gameId = consoleGameServiceFeign.createGame(typeId, gameName, price, gameIntroduction, gameDate, gamePublisher, images, tags);
            if (gameId == null) {
                return new Response<>(4004, "创建失败");
            }
            return new Response(1001, gameId);
        } catch (Exception e) {
            log.error("创建游戏失败", e);
            return new Response<>(4004, "创建失败");
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
            @RequestParam(name = "tags", required = false) String tags) {
        if (loginUser == null) {
            return new Response<>(1002, "用户未登录");
        }
        try {
            boolean result = consoleGameServiceFeign.updateGame(gameId, typeId, gameName, price, gameIntroduction, gameDate, gamePublisher, images, tags);
            return result ? new Response(1001) : new Response(4004, "更新失败");
        } catch (Exception e) {
            log.error("更新游戏失败", e);
            return new Response<>(4004, "更新失败");
        }
    }

    @RequestMapping("/delete")
    public Response deleteGame(@VerifiedUser User loginUser,
                               @RequestParam(name = "gameId") BigInteger gameId) {
        if (loginUser == null) {
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
                             @RequestParam(name = "keyword", required = false) String keyword) {
        if (loginUser == null) {
            return new Response(1002, "用户未登录");
        }
        try {
            boolean console = true;
            List<Type> types = consoleGameServiceFeign.typeList(keyword);
            List<TypeListVO> typeList = new ArrayList<>();
            for (Type type : types) {
                TypeListVO typeListVO = new TypeListVO()
                        .setTypeId(type.getId())
                        .setTypeName(type.getTypeName())
                        .setImage(type.getImage())
                        .setParentId(type.getParentId())
                        .setCreateTime(BaseUtils.timeStamp2Date(type.getCreateTime()))
                        .setUpdateTime(BaseUtils.timeStamp2Date(type.getUpdateTime()));
                typeList.add(typeListVO);
            }
            if (typeList == null) {
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
                             @RequestParam(name = "typeId") BigInteger typeId) {
        if (loginUser == null) {
            return new Response(1002, "用户未登录");
        }
        try {
            Type type = consoleGameServiceFeign.typeInfo(typeId);
            TypeDetailVO typeInfo = new TypeDetailVO()
                    .setTypeId(type.getId())
                    .setTypeName(type.getTypeName())
                    .setImage(type.getImage())
                    .setCreateTime(BaseUtils.timeStamp2Date(type.getCreateTime()))
                    .setUpdateTime(BaseUtils.timeStamp2Date(type.getUpdateTime()));
            if (typeInfo == null) {
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
                               @RequestParam(name = "parentId", required = false) BigInteger parentId) {
        if (loginUser == null) {
            return new Response(1002, "用户未登录");
        }
        try {
            BigInteger typeId = consoleGameServiceFeign.createType(typeName, image, parentId);
            if (typeId == null) {
                return new Response(4004, "创建游戏类型失败");
            }
            return new Response(1001, typeId);
        } catch (Exception e) {
            log.error("创建游戏类型失败", e);
            return new Response(4004, "创建游戏类型失败");
        }
    }

    @RequestMapping("/type/update")
    public Response updateType(@VerifiedUser User loginUser,
                               @RequestParam(name = "typeId") BigInteger typeId,
                               @RequestParam(name = "typeName") String typeName,
                               @RequestParam(name = "image") String image,
                               @RequestParam(name = "parentId", required = false) BigInteger parentId) {
        if (loginUser == null) {
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
                               @RequestParam(name = "typeId") BigInteger typeId) {
        if (loginUser == null) {
            return new Response(1002, "用户未登录");
        }
        try {
            boolean result = consoleGameServiceFeign.deleteType(typeId);
            return result ? new Response(1001) : new Response(4004, "删除失败");
        } catch (Exception e) {
            log.error("删除游戏类型失败", e);
            return new Response(4004, "删除失败");
        }
    }

    @RequestMapping("/type/tree")
    public Response typeTree(
            @VerifiedUser User loginUser,
            @RequestParam(name = "keyword") String keyword) {
        if (loginUser == null) {
            return new Response(1002, "用户未登录");
        }
        List <Type> rootTypes;
        try {
            rootTypes = consoleGameServiceFeign.rootTypes();
        } catch (Exception e) {
            log.error("获取根类型失败", e);
            return new Response(4004);
        }
        List<TypeTreeVO> typeTreeList = new ArrayList<>();
        for (Type rootType : rootTypes) {
            TypeTreeVO typeTreeVO = buildTree(rootType, keyword);
            if (typeTreeVO != null){
                typeTreeList.add(typeTreeVO);
            }
        }
        return new Response(1001, typeTreeList);

    }

    private TypeTreeVO buildTree(Type type, String keyword) {
        TypeTreeVO typeTreeVO = new TypeTreeVO();
        typeTreeVO.setImage(type.getImage());
        typeTreeVO.setTypeId(type.getId());
        typeTreeVO.setTypeName(type.getTypeName());
        List<Type> children = consoleGameServiceFeign.childrenList(type.getId());
        List<TypeTreeVO> childrenList = new ArrayList<>();

        // 递归构建子节点
        for (Type child : children) {
            TypeTreeVO childTreeVO = buildTree(child, keyword);
            if (childTreeVO != null) {
                childrenList.add(childTreeVO);
            }
        }
        typeTreeVO.setChildrenList(childrenList);

        // 根据关键字过滤
        if (keyword != null && !keyword.isEmpty()) {
            if (!typeTreeVO.getTypeName().contains(keyword) && childrenList.isEmpty()) {
                return null;
            }
        }

        return typeTreeVO;
    }
}
