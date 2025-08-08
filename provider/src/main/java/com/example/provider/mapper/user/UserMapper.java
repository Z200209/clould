package com.example.provider.mapper.user;


import com.example.common.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigInteger;

@Mapper
public interface UserMapper {
    @Select("select * from user1 where id = #{id}")
    User getUserById(@Param("id") BigInteger id);

    @Select("select * from user1 where phone = #{phone}")
    User getUserByPhone(@Param("phone") String phone);

    int update(User user);

    int insert(User user);

    int delete(BigInteger id);

    @Select("select * from user1 where phone = #{phone} and password = #{password}")
    User login(@Param("phone") String phone,@Param("password") String password);





}
