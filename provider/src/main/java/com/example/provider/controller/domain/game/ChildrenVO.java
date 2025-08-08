package com.example.provider.controller.domain.game;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;
@Data
@Accessors(chain = true)
public class ChildrenVO {
    private BigInteger typeId;
    private String typeName;
    private String image;

}
