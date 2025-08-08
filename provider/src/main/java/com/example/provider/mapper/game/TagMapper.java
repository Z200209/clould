package com.example.provider.mapper.game;


import com.example.common.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface TagMapper {
    // 根据ID查询操作
    @Select("SELECT * FROM tag WHERE id = #{tagId} AND is_deleted=0")
    Tag getById(BigInteger tagId);

    // 根据ID提取记录
    @Select("SELECT * FROM tag WHERE id = #{tagId}")
    Tag extractById(BigInteger tagId);

    // 根据标签名称查询
    @Select("SELECT * FROM tag WHERE name = #{name} AND is_deleted=0")
    Tag getByName(String name);

    /**
     * 根据标签ID列表查询标签
     */
    List<Tag> getByIds(@Param("tagIds") List<BigInteger> tagIds);

    // 插入记录
    int insert(@Param("tag") Tag tag);

    // 更新记录
    int update(@Param("tag") Tag tag);

    // 删除操作
    @Update("UPDATE tag SET update_time = #{updateTime}, is_deleted = 1 WHERE id = #{tagId}")
    int delete(@Param("tagId") BigInteger tagId, @Param("updateTime") Integer updateTime);
    
    // 获取所有标签
    List<Tag> getAllTag(@Param("keyword") String keyword);
}