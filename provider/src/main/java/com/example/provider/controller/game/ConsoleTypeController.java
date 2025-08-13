package com.example.provider.controller.game;


import com.example.common.dto.TypeListVO;
import com.example.common.entity.Game;
import com.example.common.entity.Type;
import com.example.common.utils.BaseUtils;
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
    public List<TypeListVO> typeList(@RequestParam(name = "keyword", required=false) String keyword) {
        
        // 获取所有类型
        List<Type> types;
        try {
            types = typeService.getAllType(keyword);
        } catch (Exception e) {
            throw new RuntimeException("获取类型列表失败: {}",e);
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
        
        return typeList;
    }

    /**
     * 获取类型详情
     */
    @RequestMapping("/info")
    public TypeDetailVO typeInfo(@RequestParam(name = "typeId") BigInteger typeId) {
        
        // 获取类型信息
        Type type;
        try {
            type = typeService.getById(typeId);
        } catch (Exception e) {
            throw new RuntimeException("获取类型详情失败: {}",e);
        }
        
        if (type == null) {
            throw new RuntimeException("未找到游戏类型");
        }
        
        // 格式化时间
        String formattedCreateTime;
        String formattedUpdateTime;
        try {
            formattedCreateTime = BaseUtils.timeStamp2DateGMT(type.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
            formattedUpdateTime = BaseUtils.timeStamp2DateGMT(type.getUpdateTime(), "yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            throw new RuntimeException("格式化时间失败: {}", e);
        }
        
        // 构建响应对象
        TypeDetailVO typeDetailVO = new TypeDetailVO()
                .setTypeId(type.getId())
                .setTypeName(type.getTypeName())
                .setParentId(type.getParentId())
                .setImage(type.getImage())
                .setCreateTime(formattedCreateTime)
                .setUpdateTime(formattedUpdateTime);
                
        return  typeDetailVO;
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
            throw new RuntimeException("获取类型下游戏列表失败: {}",e);
        }
        
        if (games != null && !games.isEmpty()) {
            throw new RuntimeException("该类型下有游戏，不能删除");
        }
        
        // 删除类型
        int result;
        try {
            result = typeService.delete(typeId);
        } catch (Exception e) {
            throw new RuntimeException("删除类型失败: {}",  e);
        }
        
        if (result == 1) {
            return true;
        } else {
            throw new RuntimeException("删除类型失败");
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
    public List<TypeTreeVO> typeTree(
            @RequestParam(name = "keyword", required = false) String keyword) {
        List<Type> rootTypes;
        try {
             rootTypes = typeService.getRootTypes();
        }
        catch (Exception e) {
            throw new RuntimeException("获取根类型列表失败: {}", e);
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
        return typeTreeList;
    }


}
