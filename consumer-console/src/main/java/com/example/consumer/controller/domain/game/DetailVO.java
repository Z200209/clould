package com.example.consumer.controller.domain.game;


import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;
import java.util.List;

@Data
@Accessors(chain = true)
public class DetailVO {
    private BigInteger gameId;       // 游戏 ID
    private String gameName;         // 游戏名称
    private Float price;             // 游戏价格
    private List<BaseIntroductionVO> gameIntroduction; // 游戏简介
    private String gameDate;         // 游戏发布日期
    private String gamePublisher;    // 游戏发行商
    private List<String> images;     // 游戏轮播图（列表形式）
    private String createTime;
    private String updateTime;
    private String typeName;
    private String typeImage;
    private List<String> tags;       // 游戏标签列表
}
