package com.example.consumer.feign;

import com.example.common.annotations.VerifiedUser;
import com.example.common.dto.TypeListVO;
import com.example.common.entity.User;
import com.example.common.utils.Response;
import com.example.provider.controller.domain.game.GameInfoVO;
import com.example.provider.controller.domain.game.GameListVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;
import java.util.List;

/**
 * 游戏服务Feign客户端
 * 用于调用游戏微服务的远程接口
 * 严格按照provider接口定义
 * 注意：game-service有context-path=/game，需要在path中指定
 */
@FeignClient(name = "provider", contextId = "appGameServiceFeign", path = "/app/game")
public interface AppGameServiceFeign {
    /**
     * 获取游戏信息
     *
     * @param gameId
     * @return
     */
    @RequestMapping("/info")
    Response<GameInfoVO> getAppGameInfo(
            @VerifiedUser User loginUser,
            @RequestParam(name = "gameId") BigInteger gameId);

    /**
     * 获取游戏列表
     *
     * @param keyword
     * @param typeId
     * @param wp
     * @return
     */
    @RequestMapping("/list")
    Response<List<GameListVO>> getAppGameList(
            @VerifiedUser User loginUser,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "typeId", required = false) BigInteger typeId,
            @RequestParam(name = "wp", required = false) String wp
    );
    /**
     * 获取游戏类型列表
     *
     * @param keyword
     * @return
     */
    @RequestMapping("/type/list")
    Response<List<TypeListVO>> getAppGameTypeList(
            @VerifiedUser User loginUser,
            @RequestParam(name = "keyword", required = false) String keyword
    );
    /**
     * 获取游戏类型子类型列表
     *
     * @param typeId
     * @return
     */
    @RequestMapping("/type/childrenList")
    Response<List<TypeListVO>> getAppGameTypeChildren(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeId") BigInteger typeId
    );
}
