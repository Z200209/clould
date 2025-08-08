package com.example.provider.service.game;


import com.alibaba.fastjson.JSON;
import com.example.common.annotations.DataSource;
import com.example.common.config.mysql.DataSourceType;
import com.example.common.dto.IntroductionDTO;
import com.example.common.entity.Game;
import com.example.common.entity.Type;

import com.example.provider.mapper.game.GameMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@Service
public class GameService {

    @Resource
    private GameMapper mapper;
    @Autowired
    private TypeService typeService;
    @Autowired
    private TagService tagService;


    @DataSource(DataSourceType.SLAVE)
    public Game getById(BigInteger id) {
        return mapper.getById(id);
    }
    
    @DataSource(DataSourceType.SLAVE)
    public Game extractById(BigInteger id) {
        return mapper.extractById(id);
    }

    @DataSource(DataSourceType.MASTER)
    @Transactional
    public int insert(Game game) {
        return mapper.insert(game);
    }
    
    @DataSource(DataSourceType.MASTER)
    @Transactional
    public int update(Game game) {
        return mapper.update(game);
    }
    
    @DataSource(DataSourceType.MASTER)
    @Transactional
    public int delete(BigInteger id) {
        int time = (int) (System.currentTimeMillis() / 1000);
        if (id == null){
            throw new RuntimeException("id 不能为空");
        }
        return mapper.delete(id, time);
    }


    @DataSource(DataSourceType.SLAVE)
    public List<Game> getAllGame(Integer page, Integer pageSize, String keyword, BigInteger typeId) {
        List <BigInteger> typeIdList = typeService.getTypeIdList(keyword);
        StringBuilder typeIdString = new StringBuilder();
        for (BigInteger bigInteger : typeIdList) {
            if (!typeIdString.isEmpty()){
                typeIdString.append(",");
            }
            typeIdString.append(bigInteger.toString());
        }
        String ids = typeIdString.toString();

        return mapper.getAll((page-1) * pageSize, pageSize, keyword,typeId, ids);
    }

    @DataSource(DataSourceType.SLAVE)
    public List<Game> getAllGameByTypeId(BigInteger typeId) {
        return mapper.getAllGameByTypeId(typeId);
    }


    @DataSource(DataSourceType.MASTER)
    @Transactional
    public BigInteger edit (BigInteger id, String gameName, Float price, String gameIntroduction, String gameDate, String gamePublisher, String images, BigInteger typeId, String tags) {
        try {
            List<IntroductionDTO> check = JSON.parseArray(gameIntroduction, IntroductionDTO.class);
            for (IntroductionDTO introductionDTO : check) {
                if (!IntroductionDefine.isIntroductionType(introductionDTO.getType())) {
                    throw new RuntimeException("gameIntroduction格式错误");
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        if (gameName == null || gameName.isEmpty()) {
            throw new RuntimeException("gameName 不能为空");
        }
        if (price == null || price < 0) {
            throw new RuntimeException("price 不能为空");
        }
        if (gameDate == null || gameDate.isEmpty()) {
            throw new RuntimeException("gameDate 不能为空");
        }
        if (gamePublisher == null || gamePublisher.isEmpty()) {
            throw new RuntimeException("gamePublisher 不能为空");
        }
        if (images == null || images.isEmpty()) {
            throw new RuntimeException("images 不能为空");
        }
        if(typeId != null) {
            Type type = typeService.getById(typeId);
            if (type == null){
                throw new RuntimeException("typeId不存在");
            }
        }
        if(tags == null || tags.isEmpty()){
            throw new RuntimeException("tags 不能为空");
        }
        int time = (int) (System.currentTimeMillis() / 1000);
        Game game = new Game();
        game.setGameName(gameName);
        game.setPrice(price);
        game.setGameIntroduction(gameIntroduction);
        game.setGameDate(gameDate);
        game.setGamePublisher(gamePublisher);
        game.setImages(images);
        game.setUpdateTime(time);
        game.setTypeId(typeId);
        if (id == null){
            game.setCreateTime(time);
            game.setIsDeleted(0);
            int result = insert(game);
            if (result == 0){
                throw new RuntimeException("插入失败");
            }
            id = game.getId();
        }
        else {
            game.setId(id);
            int result = update(game);
            if (result == 0){
                throw new RuntimeException("id不存在");
            }
        }
        tagService.updateGameTags(id, tags);
        return game.getId();

    }



    @DataSource(DataSourceType.SLAVE)
    public Integer getTotalCount(String keyword) {
        return mapper.getTotalCount(keyword);
    }

}
