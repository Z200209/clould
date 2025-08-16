package com.example.provider.controller.game;

import com.example.common.entity.Game;
import com.example.common.entity.Tag;
import com.example.provider.service.game.GameService;
import com.example.provider.service.game.TagService;
import com.example.provider.service.game.TypeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 游戏控制器
 */
@Slf4j
@RestController
@RequestMapping("/game")
public class GameController {

    @Resource
    private GameService gameService;

    @Resource
    private TagService tagService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取游戏详情
     */
    @RequestMapping("/info")
    public Game gameInfo(@RequestParam(name = "gameId") BigInteger gameId) {
        Game game;
        try {
            game = gameService.getById(gameId);
        } catch (Exception e) {
            throw new RuntimeException("获取游戏信息失败");
        }
        if (game == null) {
            throw new RuntimeException("未找到游戏信息");
        }

        return game;
    }

    /**
     * 获取游戏列表
     */
    @RequestMapping("/list")
    public List<Game> gameList(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "typeId", required = false) BigInteger typeId,
            @RequestParam(name = "page", defaultValue = "1") Integer page) {

        int pageSize = 10;

        // 获取游戏列表
        List<Game> gameList;
        try {
            gameList = gameService.getAllGame(page, pageSize, keyword, typeId);
        } catch (Exception e) {
            log.error("获取游戏列表失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取游戏列表失败");
        }
        return gameList;
    }

    /**
     * 从缓存中获取游戏列表
     */
    @RequestMapping("/listFromRedis")
    public Object appGameList(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "typeId", required = false) BigInteger typeId,
            @RequestParam(name = "currentPage", required = false) Integer currentPage) {
        // 构建缓存键: game_list-关键词-typeId-page
        String cacheKey = "game_list-" +
                (keyword != null ? keyword : "") + "-" +
                (typeId != null ? typeId.toString() : "") + "-" +
                currentPage;

        // 尝试从缓存获取数据
        try {
            Object cachedResult = redisTemplate.opsForValue().get(cacheKey);
            if (cachedResult != null) {
                log.info("从缓存获取游戏列表数据，缓存键: {}", cacheKey);
                return cachedResult;
            }
        } catch (Exception e) {
            log.info("从缓存获取数据失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 将游戏列表存入缓存
     */
    @RequestMapping("/listIntoRedis")
    public void listIntoRedis(
            @RequestParam(name = "cacheKey", required = false) String cacheKey,
            @RequestParam(name = "result", required = false) Object result) {
        try {
            redisTemplate.opsForValue().set(cacheKey, result, Duration.ofMinutes(30));
            log.info("游戏列表数据已存入缓存，缓存键: {}", cacheKey);
        } catch (Exception e) {
            log.info("存储缓存失败: {}", e.getMessage());
        }

    }

    /**
     * 创建游戏
     */
    @RequestMapping("/create")
    public BigInteger createGame(
            @RequestParam(name = "typeId", required = false) BigInteger typeId,
            @RequestParam(name = "gameName") String gameName,
            @RequestParam(name = "price") Float price,
            @RequestParam(name = "gameIntroduction") String gameIntroduction,
            @RequestParam(name = "gameDate") String gameDate,
            @RequestParam(name = "gamePublisher") String gamePublisher,
            @RequestParam(name = "images") String images,
            @RequestParam(name = "tags") String tags) {

        // 参数验证
        gameName = gameName.trim();
        gamePublisher = gamePublisher.trim();
        tags = tags.trim();

        if (gameName.isEmpty()) {
            throw new RuntimeException("游戏名称不能为空字符串");
        }

        if (price < 0) {
            throw new RuntimeException("游戏价格不能为负数");
        }

        if (gameIntroduction == null) {
            throw new RuntimeException("游戏介绍不能为空字符串");
        }

        if (tags.isEmpty()) {
            throw new RuntimeException("游戏标签不能为空字符串");
        }

        // 创建游戏
        BigInteger gameId;
        try {
            gameId = gameService.edit(null, gameName, price, gameIntroduction, gameDate, gamePublisher, images, typeId, tags);
        } catch (Exception e) {
            throw new RuntimeException("创建游戏失败");
        }

        return gameId;
    }

    /**
     * 更新游戏信息
     */
    @RequestMapping("/update")
    public BigInteger updateGame(
            @RequestParam(name = "gameId", required = false) BigInteger gameId,
            @RequestParam(name = "typeId", required = false) BigInteger typeId,
            @RequestParam(name = "gameName", required = false) String gameName,
            @RequestParam(name = "price", required = false) Float price,
            @RequestParam(name = "gameIntroduction", required = false) String gameIntroduction,
            @RequestParam(name = "gameDate", required = false) String gameDate,
            @RequestParam(name = "gamePublisher", required = false) String gamePublisher,
            @RequestParam(name = "images", required = false) String images,
            @RequestParam(name = "tags", required = false) String tags) {

        // 参数验证
        if (gameId == null) {
            throw new RuntimeException("游戏ID不能为空");
        }

        if (gameName != null) {
            gameName = gameName.trim();
            if (gameName.isEmpty()) {
                throw new RuntimeException("游戏名称不能为空字符串");
            }
        }

        if (gamePublisher != null) {
            gamePublisher = gamePublisher.trim();
        }

        if (tags != null) {
            tags = tags.trim();
        }

        if (price != null && price < 0) {
            throw new RuntimeException("游戏价格不能为负数");
        }

        if (gameIntroduction != null && gameIntroduction.trim().isEmpty()) {
            throw new RuntimeException("游戏介绍不能为空字符串");
        }

        // 检查游戏是否存在
        Game existingGame;
        try {
            existingGame = gameService.getById(gameId);
        } catch (Exception e) {
            throw new RuntimeException("获取游戏信息失败");
        }

        if (existingGame == null) {
            throw new RuntimeException("未找到游戏");
        }

        if (tags != null && tags.isEmpty()) {
            throw new RuntimeException("游戏标签不能为空字符串");
        }

        String finalGameName = gameName != null ? gameName : existingGame.getGameName();
        Float finalPrice = price != null ? price : existingGame.getPrice();
        String finalGameIntroduction = gameIntroduction != null ? gameIntroduction : existingGame.getGameIntroduction();
        String finalGameDate = gameDate != null ? gameDate : existingGame.getGameDate();
        String finalGamePublisher = gamePublisher != null ? gamePublisher : existingGame.getGamePublisher();
        String finalImages = images != null ? images : existingGame.getImages();
        BigInteger finalTypeId = typeId != null ? typeId : existingGame.getTypeId();

        String finalTags = tags;
        if (tags == null) {
            // 获取现有标签
            List<Tag> existingTags = tagService.getTagsByGameId(gameId);
            if (!existingTags.isEmpty()) {
                finalTags = existingTags.stream()
                        .map(Tag::getName)
                        .collect(Collectors.joining(","));
            }
        }

        try {
            gameService.edit(gameId, finalGameName, finalPrice, finalGameIntroduction, finalGameDate, finalGamePublisher, finalImages, finalTypeId, finalTags);
        } catch (Exception e) {
            throw new RuntimeException("更新游戏失败");
        }

        return gameId;
    }

    /**
     * 删除游戏
     */
    @RequestMapping("/delete")
    public boolean deleteGame(
            @RequestParam(name = "gameId") BigInteger gameId) {

        try {
            // 检查游戏是否存在
            Game game = gameService.getById(gameId);
            if (game == null) {
                throw new RuntimeException("未找到游戏");
            }

            // 删除游戏
            int result = gameService.delete(gameId);
            if (result == 1) {
                return true;
            } else {
                throw new RuntimeException("游戏删除失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("删除游戏失败", e);
        }
    }

    /**
     * 获取游戏条数
     *
     * @param keyword
     * @return
     */
    @RequestMapping("/totalCount")
    public Integer getTotalCount(@RequestParam(name = "keyword", required = false) String keyword) {
        try {
            return gameService.getTotalCount(keyword);
        } catch (Exception e) {
            throw new RuntimeException("获取游戏条数失败");
        }
    }

    /**
     * 根据游戏id获取标签列表
     *
     * @param gameId
     * @return
     */
    @RequestMapping("/getTagsByGameId")
    public List<Tag> getTagsByGameId(@RequestParam(name = "gameId") BigInteger gameId) {
        if (gameId == null) {
            throw new RuntimeException("游戏ID不能为空");
        }
        try {
            return tagService.getTagsByGameId(gameId);
        } catch (Exception e) {
            throw new RuntimeException("获取标签列表失败");
        }
    }


}