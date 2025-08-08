package com.example.common.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class GameTagRelation {
    // 主键ID
    private BigInteger id;
    // 游戏ID
    private BigInteger gameId;
    // 标签ID
    private BigInteger tagId;
    // 创建时间
    private Integer createTime;
    // 更新时间
    private Integer updateTime;
    // 是否删除 1-是 0-否
    private Integer isDeleted;

} 