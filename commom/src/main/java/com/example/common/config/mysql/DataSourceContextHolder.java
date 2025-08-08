package com.example.common.config.mysql;

/**
 * 数据源上下文持有者
 * 使用ThreadLocal来保存当前线程的数据源类型
 */
public class DataSourceContextHolder {
    
    private static final ThreadLocal<DataSourceType> CONTEXT_HOLDER = new ThreadLocal<>();
    
    /**
     * 设置数据源类型
     * @param dataSourceType 数据源类型
     */
    public static void setDataSourceType(DataSourceType dataSourceType) {
        CONTEXT_HOLDER.set(dataSourceType);
    }
    
    /**
     * 获取数据源类型
     * @return 数据源类型
     */
    public static DataSourceType getDataSourceType() {
        return CONTEXT_HOLDER.get();
    }
    
    /**
     * 清除数据源类型
     */
    public static void clearDataSourceType() {
        CONTEXT_HOLDER.remove();
    }
}