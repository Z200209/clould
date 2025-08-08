package com.example.provider.mapper.sms;


import com.example.common.entity.Sms;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface SmsMapper {
    
    List<Sms> findByPhone(@Param("phone") String phone);
    
    @Select("SELECT * FROM sms WHERE id = #{id}")
    Sms getSmsById(@Param("id") BigInteger id);
    
    int insert(@Param("sms")Sms sms);
    
    int update(@Param("sms")Sms sms);
    
    int delete(@Param("id") BigInteger id, @Param("time") Integer time);
}