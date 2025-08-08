package com.example.provider.mapper.sms;

import com.example.common.entity.SmsTaskCrond;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface SmsTaskCrondMapper {
    
    /**
     * 插入短信任务
     */
    int insert(@Param("smsTaskCrond") SmsTaskCrond smsTaskCrond);
    
    /**
     * 更新短信任务
     */
    int update(@Param("smsTaskCrond") SmsTaskCrond smsTaskCrond);
    
    /**
     * 更新任务状态
     */
    int updateStatus(@Param("id") BigInteger id, 
                    @Param("status") Integer status, 
                    @Param("executeTime") Integer executeTime, 
                    @Param("updateTime") Integer updateTime);
    
    /**
     * 逻辑删除任务
     */
    int delete(@Param("id") BigInteger id, @Param("time") Integer time);
    
    /**
     * 查询待执行的任务（按创建时间顺序）
     */
    List<SmsTaskCrond> findPendingTasks(@Param("limit") Integer limit);
    
    /**
     * 根据ID查询任务
     */
    SmsTaskCrond findById(@Param("id") BigInteger id);
    
    /**
     * 根据手机号查询任务
     */
    List<SmsTaskCrond> findByPhone(@Param("phone") String phone);
}