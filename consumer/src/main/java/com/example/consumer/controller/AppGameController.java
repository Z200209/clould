package com.example.consumer.controller;

import com.example.common.annotations.VerifiedUser;
import com.example.common.utils.Response;
import com.example.consumer.feign.AppGameServiceFeign;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.common.entity.User;

import java.math.BigInteger;

@Slf4j
@RestController
@RequestMapping("/app/game")
public class AppGameController {
    @Resource
    private AppGameServiceFeign appGameServiceFeign;

    @RequestMapping("/info")
    public Response getAppGameInfo(@VerifiedUser User loginUser,
                                   @RequestParam(name = "gameId") BigInteger gameId){
        return appGameServiceFeign.getAppGameInfo(loginUser, gameId);
    }

    @RequestMapping("/list")
    public Response getAppGameList(@VerifiedUser User loginUser,
                                   @RequestParam(name = "keyword", required = false) String keyword,
                                   @RequestParam(name = "typeId", required = false) BigInteger typeId,
                                   @RequestParam(name = "wp", required = false) String wp){
        return appGameServiceFeign.getAppGameList(loginUser, keyword, typeId, wp);
    }

    @RequestMapping("/type/list")
    public Response getAppGameTypeList(@VerifiedUser User loginUser,
                                       @RequestParam(name = "keyword", required = false) String keyword){
        return appGameServiceFeign.getAppGameTypeList(loginUser, keyword);
    }

    @RequestMapping("/type/childrenList")
    public Response getAppGameTypeChildren(@VerifiedUser User loginUser,
                                          @RequestParam(name = "typeId") BigInteger typeId){
        return appGameServiceFeign.getAppGameTypeChildren(loginUser, typeId);
    }

}
