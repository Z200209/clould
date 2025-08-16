package com.example.consumer.controller.domain.game;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ListVO {
    private List<ConsoleGameVO> gameList;
    private Integer total;
    private Integer pageSize;

}
