package com.example.consumer.controller.domain.game;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class GameListVO {
    private BigInteger gameId;
    private String gameName;
    private String typeName;
    private Float price;
    private String createTime;
    private String updateTime;
}
