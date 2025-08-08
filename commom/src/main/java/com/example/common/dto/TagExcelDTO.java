package com.example.common.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * 标签表Excel导出DTO
 */
@Data
public class TagExcelDTO {
    
    @ExcelProperty("标签ID")
    private BigInteger id;
    
    @ExcelProperty("标签名称")
    private String name;
    
    @ExcelProperty("创建时间")
    private Integer createTime;
    
    @ExcelProperty("更新时间")
    private Integer updateTime;
    
    @ExcelProperty("是否删除")
    private Integer isDeleted;
}