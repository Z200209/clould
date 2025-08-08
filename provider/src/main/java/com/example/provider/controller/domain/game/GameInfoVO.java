package com.example.provider.controller.domain.game;


import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;
import java.util.List;

@Data
@Accessors(chain = true)
public class GameInfoVO {
    private BigInteger gameId;       // 游戏 ID
    private String gameName;         // 游戏名称
    private Float price;             // 游戏价格
    private List<BaseIntroductionVO> gameIntroduction; // 游戏简介
    private String gameDate;         // 游戏发布日期
    private String gamePublisher;    // 游戏发行商
    private List<String> images;     // 游戏轮播图（列表形式）
    private String typeName;
    private List<String> tags;
    private String TypeImage;

}
