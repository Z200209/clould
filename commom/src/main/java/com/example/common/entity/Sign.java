package com.example.common.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class Sign {
    private BigInteger id;
    private Integer expirationTime;
}
