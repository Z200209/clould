package com.example.common.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class Wp {
    private String keyword;
    private BigInteger typeId;
    private Integer page;
    private String ids;
    private Integer pageSize;

}
