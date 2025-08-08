package com.example.provider.controller.domain.game;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class GameListVO {
   List<GameVO> gameList;
   private String wp;
}
