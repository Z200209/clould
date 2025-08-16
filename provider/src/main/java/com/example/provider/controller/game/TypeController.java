package com.example.provider.controller.game;

import com.example.common.entity.Game;
import com.example.common.entity.Type;
import com.example.provider.service.game.GameService;
import com.example.provider.service.game.TypeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 游戏类型控制器
 */
@Slf4j
@RestController
@RequestMapping("/game/type")
public class TypeController {

    @Resource
    private TypeService typeService;

    @Resource
    private GameService gameService;

    /**
     * 获取类型列表
     */
    @RequestMapping("/list")
    public List<Type> typeList(
            @RequestParam(name = "keyword", required = false) String keyword) {

        List<Type> typeList;
        try {
            typeList = typeService.getAllType(keyword);
        } catch (Exception e) {
            throw new RuntimeException("获取类型列表失败");
        }
        return typeList;
    }

    /**
     * 获取子类型列表和对应游戏列表
     */
    @RequestMapping("/childrenList")
    public Map<String, Object> childrenList(@RequestParam(name = "typeId") BigInteger typeId) {
        // 获取子类型列表
        List<Type> childrenList;
        try {
            childrenList = typeService.getChildrenList(typeId);
        } catch (Exception e) {
            throw new RuntimeException("获取子类型列表失败");
        }

        // 获取游戏列表
        List<Game> gamesByType;
        try {
            gamesByType = gameService.getAllGameByTypeId(typeId);
        } catch (Exception e) {
            throw new RuntimeException("获取游戏列表失败");
        }

        // 构建返回对象
        Map<String, Object> result = new HashMap<>();
        result.put("childrenList", childrenList);
        result.put("gameList", gamesByType);

        return result;
    }

    /**
     * 获取类型详情
     */
    @RequestMapping("/info")
    public Type typeInfo(@RequestParam(name = "typeId") BigInteger typeId) {
        // 获取类型信息
        Type type;
        try {
            type = typeService.getById(typeId);
        } catch (Exception e) {
            throw new RuntimeException("获取类型详情失败");
        }

        if (type == null) {
            throw new RuntimeException("未找到游戏类型");
        }

        return type;
    }

    /**
     * 新增类型
     */
    @RequestMapping("/create")
    public BigInteger createType(
            @RequestParam(name = "typeName") String typeName,
            @RequestParam(name = "image") String image,
            @RequestParam(name = "parentId", required = false) BigInteger parentId) {

        // 参数验证
        typeName = typeName.trim();
        if (typeName.isEmpty()) {
            throw new RuntimeException("游戏类型名称不能为空字符串");
        }
        try {
            // 创建类型
            return typeService.edit(null, typeName, image, parentId);
        } catch (Exception e) {
            throw new RuntimeException("创建类型失败", e);
        }
    }

    /**
     * 更新类型
     */
    @RequestMapping("/update")
    public boolean updateType(
            @RequestParam(name = "typeId") BigInteger typeId,
            @RequestParam(name = "typeName") String typeName,
            @RequestParam(name = "image") String image,
            @RequestParam(name = "parentId", required = false) BigInteger parentId) {

        // 参数验证
        typeName = typeName.trim();
        if (typeName.isEmpty()) {
            throw new RuntimeException("游戏类型名称不能为空字符串");
        }

        // 检查类型是否存在
        Type type = typeService.getById(typeId);
        if (type == null) {
            throw new RuntimeException("未找到游戏类型");
        }
        try {
            // 更新类型
            typeService.edit(typeId, typeName, image, parentId);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("更新类型失败", e);
        }
    }

    /**
     * 删除类型
     */
    @RequestMapping("/delete")
    public boolean deleteType(
            @RequestParam(name = "typeId") BigInteger typeId) {

        // 参数验证
        if (typeId == null) {
            throw new RuntimeException("游戏类型ID不能为空");
        }

        // 检查该类型下是否有游戏
        List<Game> games;
        try {
            games = gameService.getAllGameByTypeId(typeId);
        } catch (Exception e) {
            throw new RuntimeException("获取类型下游戏列表失败");
        }

        if (games != null && !games.isEmpty()) {
            throw new RuntimeException("该类型下有游戏，不能删除");
        }

        // 删除类型
        int result;
        try {
            result = typeService.delete(typeId);
        } catch (Exception e) {
            throw new RuntimeException("删除类型失败");
        }

        if (result == 1) {
            return true;
        } else {
            throw new RuntimeException("删除类型失败");
        }
    }

    /**
     * 获取类型树
     */
    @RequestMapping("/rootTree")
    public List<Type> typeTree() {
        List<Type> rootTypes;
        try {
            rootTypes = typeService.getRootTypes();
        } catch (Exception e) {
            throw new RuntimeException("获取根类型列表失败");
        }
        return rootTypes;
    }

    /**
     * 根据ids获取类型列表
     */
    @RequestMapping("/listByIds")
    public List<Type> typeListByIds(
            @RequestParam(name = "typeIds") Set<BigInteger> typeIds) {
        List<Type> typeList;
        try {
            typeList = typeService.getTypeByIds(typeIds);
        } catch (Exception e) {
            throw new RuntimeException("获取类型列表失败");
        }
        return typeList;
    }


}