package com.example.consumerapp.controller.domain.game;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;
import java.util.List;
@Data
@Accessors(chain = true)
public class TypeTreeVO {
    private String image;
    private BigInteger typeId;
    private String typeName;
    private List<TypeTreeVO> childrenList;
}
