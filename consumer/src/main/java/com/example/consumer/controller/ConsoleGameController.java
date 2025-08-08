package com.example.consumer.controller;

import com.example.common.annotations.VerifiedUser;
import com.example.common.entity.User;
import com.example.common.utils.Response;
import com.example.consumer.feign.ConsoleGameServiceFeign;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@Slf4j
@RestController
@RequestMapping("/console/game")
public class ConsoleGameController {
    @Resource
    private ConsoleGameServiceFeign consoleGameServiceFeign;

    @RequestMapping("/create")
    public Response createGame(@VerifiedUser User loginUser,
                               @RequestParam(name = "typeId", required = false) BigInteger typeId,
                               @RequestParam(name = "gameName") String gameName,
                               @RequestParam(name = "price") Float price,
                               @RequestParam(name = "gameIntroduction") String gameIntroduction,
                               @RequestParam(name = "gameDate") String gameDate,
                               @RequestParam(name = "gamePublisher") String gamePublisher,
                               @RequestParam(name = "images") String images,
                               @RequestParam(name = "tags") String tags
                               )
        {
        return consoleGameServiceFeign.createGame(loginUser, typeId, gameName, price, gameIntroduction, gameDate, gamePublisher, images, tags);
    }

    @RequestMapping("/update")
    public Response updateGame(@VerifiedUser User loginUser,
                              @RequestParam(name = "gameId", required = false) BigInteger gameId,
                              @RequestParam(name = "typeId", required = false) BigInteger typeId,
                              @RequestParam(name = "gameName", required = false) String gameName,
                              @RequestParam(name = "price", required = false) Float price,
                              @RequestParam(name = "gameIntroduction", required = false) String gameIntroduction,
                               @RequestParam(name = "gameDate", required = false) String gameDate,
                               @RequestParam(name = "gamePublisher", required = false) String gamePublisher,
                               @RequestParam(name = "images", required = false) String images,
                               @RequestParam(name = "tags", required = false) String tags){
        return consoleGameServiceFeign.updateGame(loginUser, gameId, typeId, gameName, price, gameIntroduction, gameDate, gamePublisher, images, tags);
    }

    @RequestMapping("/list")
    public Response gameList(@VerifiedUser User loginUser,
                            @RequestParam(name = "page", defaultValue = "1") Integer page,
                            @RequestParam(name = "keyword", required = false) String keyword,
                            @RequestParam(name = "typeId", required = false) BigInteger typeId){
        return consoleGameServiceFeign.gameList(loginUser, page, keyword, typeId);
    }

    @RequestMapping("/info")
    public Response gameInfo(@VerifiedUser User loginUser,
                           @RequestParam(name = "gameId") BigInteger gameId){
        return consoleGameServiceFeign.gameInfo(loginUser, gameId);
    }

    @RequestMapping("/delete")
    public Response deleteGame(@VerifiedUser User loginUser,
                                @RequestParam(name = "gameId") BigInteger gameId) {
        return consoleGameServiceFeign.deleteGame(loginUser, gameId);
    }

    @RequestMapping("/type/list")
    public Response typeList(@VerifiedUser User loginUser,
                            @RequestParam(name = "keyword", required=false) String keyword){
        return consoleGameServiceFeign.typeList(loginUser, keyword);
    }

    @RequestMapping("/type/info")
    public Response typeInfo(@VerifiedUser User loginUser,
                             @RequestParam(name = "typeId") BigInteger typeId){
        return consoleGameServiceFeign.typeInfo(loginUser, typeId);
    }

    @RequestMapping("/type/create")
    public Response createType(@VerifiedUser User loginUser,
                             @RequestParam(name = "typeName") String typeName,
                             @RequestParam(name = "image") String image,
                             @RequestParam(name = "parentId", required = false) BigInteger parentId){
        return consoleGameServiceFeign.createType(loginUser, typeName, image, parentId);
    }

    @RequestMapping("/type/update")
    public Response updateType(@VerifiedUser User loginUser,
                               @RequestParam(name = "typeId") BigInteger typeId,
                               @RequestParam(name = "typeName") String typeName,
                               @RequestParam(name = "image") String image,
                               @RequestParam(name = "parentId", required = false) BigInteger parentId){
        return consoleGameServiceFeign.updateType(loginUser, typeId, typeName, image, parentId);
    }

    @RequestMapping("/type/delete")
    public Response deleteType(@VerifiedUser User loginUser,
                             @RequestParam(name = "typeId") BigInteger typeId){
        return consoleGameServiceFeign.deleteType(loginUser, typeId);
    }

    @RequestMapping("/type/tree")
    public Response typeTree(@VerifiedUser User loginUser,
                             @RequestParam(name = "keyword", required = false) String keyword){
        return consoleGameServiceFeign.typeTree(loginUser, keyword);
    }

}
