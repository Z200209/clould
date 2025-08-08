package com.example.provider.controller.domain.game;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;


@Data
@Accessors(chain = true)
public class GameVO {
   private BigInteger gameId;
   private String gameName;
   private ImageVO image;
   private String typeName;
}
