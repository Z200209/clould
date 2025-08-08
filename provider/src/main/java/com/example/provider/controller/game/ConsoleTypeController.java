package com.example.provider.controller.game;


import com.example.common.annotations.VerifiedUser;
import com.example.common.dto.TypeListVO;
import com.example.common.entity.Game;
import com.example.common.entity.Type;
import com.example.common.entity.User;
import com.example.common.utils.BaseUtils;
import com.example.common.utils.Response;
import com.example.provider.controller.domain.game.TypeDetailVO;
import com.example.provider.controller.domain.game.TypeTreeVO;
import com.example.provider.service.game.GameService;
import com.example.provider.service.game.TypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 游戏类型控制器
 */
@Slf4j
@RestController("consoleTypeController")
@RequestMapping("/console/game/type")
public class ConsoleTypeController {
    @Autowired
    private TypeService typeService;
    @Autowired
    private GameService gameService;

    /**
     * 获取类型列表
     */
    @RequestMapping("/list")
    public Response typeList(@VerifiedUser User loginUser,
                             @RequestParam(name = "keyword", required=false) String keyword) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试获取类型列表");
            return new Response(1002);
        }
        
        // 获取所有类型
        List<Type> types;
        try {
            types = typeService.getAllType(keyword);
        } catch (Exception e) {
            log.error("获取类型列表失败: {}", e.getMessage(), e);
            return new Response(4004);
        }
        
        // 构建类型列表
        List<TypeListVO> typeList = new ArrayList<>();
        for (Type type : types) {
                String formattedCreateTime = BaseUtils.timeStamp2DateGMT(type.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
                String formattedUpdateTime = BaseUtils.timeStamp2DateGMT(type.getUpdateTime(), "yyyy-MM-dd HH:mm:ss");
                TypeListVO typeListVO = new TypeListVO()
                        .setTypeId(type.getId())
                        .setTypeName(type.getTypeName())
                        .setImage(type.getImage())
                        .setParentId(type.getParentId())
                        .setCreateTime(formattedCreateTime)
                        .setUpdateTime(formattedUpdateTime);
                typeList.add(typeListVO);
        }
        
        return new Response(1001, typeList);
    }

    /**
     * 获取类型详情
     */
    @RequestMapping("/info")
    public Response  typeInfo(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeId") BigInteger typeId) {
        
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试获取类型详情");
            return new Response(1002);
        }
        
        // 获取类型信息
        Type type;
        try {
            type = typeService.getById(typeId);
        } catch (Exception e) {
            log.error("获取类型详情失败: {}", e.getMessage(), e);
            return new Response(4004);
        }
        
        if (type == null) {
            log.info("未找到游戏类型：{}", typeId);
            return new Response(4006);
        }
        
        // 格式化时间
        String formattedCreateTime;
        String formattedUpdateTime;
        try {
            formattedCreateTime = BaseUtils.timeStamp2DateGMT(type.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
            formattedUpdateTime = BaseUtils.timeStamp2DateGMT(type.getUpdateTime(), "yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            log.error("格式化时间失败: {}", e.getMessage(), e);
            return new Response(4004);
        }
        
        // 构建响应对象
        TypeDetailVO typeDetailVO = new TypeDetailVO()
                .setTypeId(type.getId())
                .setTypeName(type.getTypeName())
                .setParentId(type.getParentId())
                .setImage(type.getImage())
                .setCreateTime(formattedCreateTime)
                .setUpdateTime(formattedUpdateTime);
                
        return new Response(1001, typeDetailVO);
    }

    /**
     * 新增类型
     */
    @RequestMapping("/create")
    public Response createType(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeName") String typeName,
            @RequestParam(name = "image") String image,
            @RequestParam(name = "parentId", required = false) BigInteger parentId) {

        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试创建类型");
            return new Response(1002);
        }


            // 参数验证
            typeName = typeName.trim();
            if (typeName.isEmpty()) {
                log.info("游戏类型名称不能为空字符串");
                return new Response(4005);
            }
        try {
            // 创建类型
            BigInteger typeId = typeService.edit(null, typeName, image, parentId);
            return new Response(1001);
        } catch (Exception e) {
            log.error("创建类型失败", e);
            return new Response(4004);
        }
    }

    /**
     * 更新类型
     */
    @RequestMapping("/update")
    public Response updateType(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeId") BigInteger typeId,
            @RequestParam(name = "typeName") String typeName,
            @RequestParam(name = "image") String image,
            @RequestParam(name = "parentId", required = false) BigInteger parentId) {

        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试更新类型");
            return new Response(1002);
        }


            // 参数验证
            typeName = typeName.trim();
            if (typeName.isEmpty()) {
                log.info("游戏类型名称不能为空字符串");
                return new Response(4005);
            }

            // 检查类型是否存在
            Type type = typeService.getById(typeId);
            if (type == null) {
                log.info("未找到游戏类型：{}", typeId);
                return new Response(4006);
            }
        try {
            // 更新类型
            typeService.edit(typeId, typeName, image, parentId);
            return new Response(1001);
        } catch (Exception e) {
            log.error("更新类型失败", e);
            return new Response(4004);
        }
    }

    /**
     * 删除类型
     */
    @RequestMapping("/delete")
    public Response deleteType(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeId") BigInteger typeId) {
        
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试删除类型");
            return new Response(1002);
        }
        
        // 参数验证
        if (typeId == null) {
            log.info("游戏类型ID不能为空");
            return new Response(4005);
        }
        
        // 检查该类型下是否有游戏
        List<Game> games;
        try {
            games = gameService.getAllGameByTypeId(typeId);
        } catch (Exception e) {
            log.error("获取类型下游戏列表失败: {}", e.getMessage(), e);
            return new Response(4004);
        }
        
        if (games != null && !games.isEmpty()) {
            log.info("该类型下有游戏，不能删除：{}", typeId);
            return new Response(4005);
        }
        
        // 删除类型
        int result;
        try {
            result = typeService.delete(typeId);
        } catch (Exception e) {
            log.error("删除类型失败: {}", e.getMessage(), e);
            return new Response(4004);
        }
        
        if (result == 1) {
            return new Response(1001);
        } else {
            return new Response(4004);
        }
    }

    private TypeTreeVO buildTree(Type type, String keyword) {
        TypeTreeVO typeTreeVO = new TypeTreeVO();
        typeTreeVO.setImage(type.getImage());
        typeTreeVO.setTypeId(type.getId());
        typeTreeVO.setTypeName(type.getTypeName());
        List<Type> children = typeService.getChildrenList(type.getId());
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



    @RequestMapping("/tree")
    public Response typeTree(
            @VerifiedUser User loginUser,
            @RequestParam(name = "keyword", required = false) String keyword) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试获取类型树");
            return new Response(1002);
        }
        List<Type> rootTypes;
        try {
             rootTypes = typeService.getRootTypes();
        }
        catch (Exception e) {
            log.error("获取根类型列表失败: {}", e.getMessage(), e);
            return new Response(4004);
        }

        List<TypeTreeVO> typeTreeList = new ArrayList<>();

        // 遍历根节点，递归构建类型树
        for (Type rootType : rootTypes) {
            // 递归构建当前节点及其子节点
            TypeTreeVO typeTreeVO = buildTree(rootType, keyword);
            if (typeTreeVO != null) {
                typeTreeList.add(typeTreeVO);
            }
        }
        return new Response(1001, typeTreeList);
    }


}
