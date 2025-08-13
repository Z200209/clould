package com.example.consumerapp.feign;

import com.example.common.annotations.VerifiedUser;
import com.example.common.entity.User;
import com.example.provider.controller.domain.game.ChildrenListVO;
import com.example.provider.controller.domain.game.GameInfoVO;
import com.example.provider.controller.domain.game.GameListVO;
import com.example.provider.controller.domain.game.TypeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;
import java.util.List;

/**
 * 游戏服务Feign客户端
 * 用于调用游戏微服务的远程接口
 */
@FeignClient(name = "provider", contextId = "appGameServiceFeign", path = "/app/game")
public interface AppGameServiceFeign {
    /**
     * 获取游戏信息
     *
     * @param gameId
     *
     * @return
     */
    @RequestMapping("/info")
    GameInfoVO getAppGameInfo(@RequestParam(name = "gameId") BigInteger gameId);

    /**
     * 获取游戏列表
     *
     * @param keyword
     * @param typeId
     * @param wp
     * @return
     */
    @RequestMapping("/list")
    GameListVO getAppGameList(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "typeId", required = false) BigInteger typeId,
            @RequestParam(name = "wp", required = false) String wp
    );
    /**
     * 获取游戏类型列表
     *
     * @param keyword
     */
    @RequestMapping("/type/list")
    List<TypeVO> getAppGameTypeList(
            @RequestParam(name = "keyword", required = false) String keyword
    );
    /**
     * 获取游戏类型子类型列表
     *
     * @param typeId
     * @return
     */
    @RequestMapping("/type/childrenList")
    ChildrenListVO getAppGameTypeChildren(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeId") BigInteger typeId
    );
}
