package com.example.common.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * 分类表Excel导出DTO
 */
@Data
public class TypeExcelDTO {
    
    @ExcelProperty("ID")
    private BigInteger id;
    
    @ExcelProperty("父级ID")
    private BigInteger parentId;
    
    @ExcelProperty("分类名称")
    private String typeName;
    
    @ExcelProperty("图片")
    private String image;
    
    @ExcelProperty("创建时间")
    private Integer createTime;
    
    @ExcelProperty("更新时间")
    private Integer updateTime;
    
    @ExcelProperty("是否删除")
    private Integer isDeleted;
}