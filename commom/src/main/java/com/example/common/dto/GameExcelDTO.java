package com.example.common.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * 游戏表Excel导出DTO
 */
@Data
public class GameExcelDTO {
    
    @ExcelProperty("游戏ID")
    private BigInteger id;
    
    @ExcelProperty("游戏名称")
    private String gameName;
    
    @ExcelProperty("价格")
    private Float price;
    
    @ExcelProperty("游戏介绍")
    private String gameIntroduction;
    
    @ExcelProperty("发布日期")
    private String gameDate;
    
    @ExcelProperty("发行商")
    private String gamePublisher;
    
    @ExcelProperty("图片")
    private String images;
    
    @ExcelProperty("分类ID")
    private BigInteger typeId;
    
    @ExcelProperty("创建时间")
    private Integer createTime;
    
    @ExcelProperty("更新时间")
    private Integer updateTime;
    
    @ExcelProperty("是否删除")
    private Integer isDeleted;
}