package com.example.provider.mapper.game;


import com.example.common.entity.Type;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface TypeMapper {
    @Select("select * from type where id = #{id} and is_deleted = 0 ")
    Type getById(BigInteger id);

    @Select("select type_name , id from type where id IN(${ids}) and is_deleted =0")
    List<Type> getTypeByIds(@Param("ids") String ids);

    @Select("select * from type where id = #{id} ")
    Type extractById(BigInteger id);

    int insert(@Param("type") Type type);
    int update(@Param("type") Type type);

    @Update("update type set is_deleted = 1, update_time=#{time} where id = #{id} limit 1")
    int delete(@Param("id")BigInteger id,@Param("time") Integer time);

    List<Type> getParentTypeList(@Param("keyword") String keyword);

    @Select("select * from type where parent_id =#{id} and is_deleted = 0")
    List<Type> getChildrenTypeList(@Param("id") BigInteger id);

    @Select("select * from type where is_deleted = 0")
    List<Type> getAllType(@Param("keyword") String keyword);

    int getTotalCount(@Param("keyword") String keyword);

    List<BigInteger> getTypeIdList(@Param("keyword") String keyword);

    List<Type> getRootTypes();

}
