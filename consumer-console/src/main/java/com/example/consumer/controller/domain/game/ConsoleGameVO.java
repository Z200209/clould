package com.example.consumer.controller.domain.game;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class ConsoleGameVO {
   private BigInteger gameId;
   private String gameName;
   private String image;
   private String typeName;
   private BigInteger typeId;

}
