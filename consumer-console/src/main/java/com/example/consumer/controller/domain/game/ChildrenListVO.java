package com.example.consumer.controller.domain.game;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
@Data
@Accessors(chain = true)
public class ChildrenListVO {
    private List<ChildrenVO> childrenList;
    private List<ChildreGameVO> gameList;
}
