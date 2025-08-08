package com.example.common.config.mysql;

/**
 * 数据源类型枚举
 */
public enum DataSourceType {
    /**
     * 主库（写）
     */
    MASTER,
    
    /**
     * 从库（读）
     */
    SLAVE
}