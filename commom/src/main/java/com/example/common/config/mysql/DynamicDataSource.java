package com.example.common.config.mysql;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源类
 * 根据当前线程的数据源类型来决定使用哪个数据源
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    /**
     * 决定当前线程使用哪个数据源
     * @return 数据源标识
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDataSourceType();
    }
}