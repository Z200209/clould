package com.example.common.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

/**
 * 游戏类型列表VO类
 * 用于返回游戏类型列表数据
 */
@Data
@Accessors(chain = true)
public class TypeListVO {
    /**
     * 类型ID
     */
    private BigInteger typeId;
    
    /**
     * 类型名称
     */
    private String typeName;
    
    /**
     * 类型图片
     */
    private String image;
    
    /**
     * 父级类型ID
     */
    private BigInteger parentId;
    
    /**
     * 创建时间
     */
    private String createTime;
    
    /**
     * 更新时间
     */
    private String updateTime;
}