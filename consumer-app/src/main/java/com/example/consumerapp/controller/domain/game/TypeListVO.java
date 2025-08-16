package com.example.consumerapp.controller.domain.game;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;


@Data
@Accessors(chain = true)
public class TypeListVO {
    private BigInteger typeId;
    private String typeName;
    private String image;
    private BigInteger parentId;
    private String createTime;
    private String updateTime;
}
