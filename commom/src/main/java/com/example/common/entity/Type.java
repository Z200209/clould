package com.example.common.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class Type {
    private BigInteger id;
    private BigInteger parentId;
    private String typeName;
    private String image;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;
}
