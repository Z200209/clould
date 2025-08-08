package com.example.provider.controller.game;


import com.example.common.annotations.VerifiedUser;
import com.example.common.entity.Game;
import com.example.common.entity.Type;
import com.example.common.entity.User;
import com.example.common.utils.Response;
import com.example.provider.controller.domain.game.ChildreGameVO;
import com.example.provider.controller.domain.game.ChildrenListVO;
import com.example.provider.controller.domain.game.ChildrenVO;
import com.example.provider.controller.domain.game.TypeVO;
import com.example.provider.service.game.GameService;
import com.example.provider.service.game.TypeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
@RestController("appTypeController")
@RequestMapping("/app/game/type")
public class AppTypeController {
    
    @Resource
    private TypeService typeService;
    
    @Resource
    private GameService gameService;
    
    /**
     * 获取类型列表
     */
    @RequestMapping("/list")
    public Response typeList(@VerifiedUser User loginUser,
                             @RequestParam(name = "keyword", required=false) String keyword) {
        // 用户验证
        if (loginUser == null) {
            return new Response(1002);
        }
        
        // 获取类型列表
        List<Type> typeList;
        try {
            typeList = typeService.getParentTypeList(keyword);
        } catch (Exception e) {
            log.error("获取类型列表失败: {}", e.getMessage(), e);
            return new Response(4004);
        }
        
        if (typeList.isEmpty()) {
            log.info("没有找到类型信息");
            return new Response(4006);
        }
        
        // 构建返回数据
        List<TypeVO> typeVOList = new ArrayList<>();
        for (Type type : typeList) {
            TypeVO typeVO = new TypeVO();
            List<ChildrenVO> childrenList = new ArrayList<>();
            
            // 获取子类型列表
            try {
                for (Type children : typeService.getChildrenList(type.getId())) {
                    ChildrenVO childrenListVO = new ChildrenVO();
                    childrenListVO.setTypeId(children.getId())
                            .setTypeName(children.getTypeName())
                            .setImage(children.getImage());
                    childrenList.add(childrenListVO);
                }
            } catch (Exception e) {
                log.error("获取子类型列表失败: {}", e.getMessage(), e);
                // 继续处理，子类型不是必须的
            }
            
            typeVO.setTypeId(type.getId())
                    .setTypeName(type.getTypeName())
                    .setImage(type.getImage())
                    .setChildrenList(childrenList);
            typeVOList.add(typeVO);
        }
        
        return new Response(1001, typeVOList);
    }

    /**
     * 获取子类型列表和对应游戏列表
     */
    @RequestMapping("/childrenList")
    public Response childrenList(@VerifiedUser User loginUser,
                                 @RequestParam(name = "typeId") BigInteger typeId) {
        // 用户验证
        if (loginUser == null) {
            return new Response(1002);
        }
        
        // 获取子类型列表
        List<Type> childrenList;
        try {
            childrenList = typeService.getChildrenList(typeId);
        } catch (Exception e) {
            log.error("获取子类型列表失败: {}", e.getMessage(), e);
            return new Response(4004);
        }
        
        List<ChildrenVO> childrenVOList = new ArrayList<>();
        for (Type children : childrenList) {
            if (children == null) {
                log.info("没有找到类型信息");
                continue;
            }
            
            ChildrenVO childrenVO = new ChildrenVO();
            childrenVO.setTypeId(children.getId())
                    .setTypeName(children.getTypeName())
                    .setImage(children.getImage());
            childrenVOList.add(childrenVO);
        }
        
        // 获取游戏列表
        List<Game> gamesByType;
        try {
            gamesByType = gameService.getAllGameByTypeId(typeId);
        } catch (Exception e) {
            log.error("获取游戏列表失败: {}", e.getMessage(), e);
            return new Response(4004);
        }
        
        List<ChildreGameVO> childreGameVOList = new ArrayList<>();
        for (Game game : gamesByType) {
            if (game == null || game.getTypeId() == null || game.getImages() == null || game.getImages().isEmpty()) {
                log.info("游戏数据不完整，跳过：{}", game.getId());
                continue;
            }
            
            Type gameType;
            try {
                gameType = typeService.getById(game.getTypeId());
                if (gameType == null) {
                    log.info("未找到游戏类型：{}", game.getTypeId());
                    continue;
                }
                
                ChildreGameVO childreGameVO = new ChildreGameVO();
                childreGameVO.setGameId(game.getId())
                        .setGameName(game.getGameName())
                        .setImage(game.getImages().split("\\$")[0])
                        .setTypeName(gameType.getTypeName());
                childreGameVOList.add(childreGameVO);
            } catch (Exception e) {
                log.error("获取游戏类型失败: {}", e.getMessage(), e);
                // 继续处理下一个游戏
            }
        }
        
        // 构建返回对象
        ChildrenListVO result = new ChildrenListVO()
                .setChildrenList(childrenVOList)
                .setGameList(childreGameVOList);
        
        return new Response(1001, result);
    }
}
