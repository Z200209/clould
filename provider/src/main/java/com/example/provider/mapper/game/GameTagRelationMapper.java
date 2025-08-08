package com.example.provider.mapper.game;


import com.example.common.entity.GameTagRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface GameTagRelationMapper {
    /**
     * 根据游戏ID查询关联关系
     */
    @Select("SELECT * FROM game_tag_relation WHERE game_id = #{gameId} AND is_deleted = 0")
    List<GameTagRelation> getByGameId(BigInteger gameId);

    /**
     * 根据游戏ID和标签ID查询关联关系
     */
    @Select("SELECT * FROM game_tag_relation WHERE game_id = #{gameId} AND tag_id = #{tagId} AND is_deleted = 0")
    GameTagRelation getByGameIdAndTagId(@Param("gameId") BigInteger gameId, @Param("tagId") BigInteger tagId);


    /**
     * 根据游戏ID查询标签ID
     */
    @Select("SELECT tag_id FROM game_tag_relation WHERE game_id = #{gameId} AND is_deleted = 0")
    List<BigInteger> getTagIdsByGameId(BigInteger gameId);

    /**
     * 新增游戏标签关联
     */
    int insert(GameTagRelation relation);

    /**
     * 批量逻辑删除游戏标签关联（根据游戏ID和不在标签ID列表中的记录）
     */
    int batchDeleteByGameIdAndNotInTagIds(@Param("gameId") BigInteger gameId,
                                         @Param("tagIds") List<BigInteger> tagIds,
                                         @Param("updateTime") Integer updateTime);

    /**
     * 逻辑删除
     */
    @Update("UPDATE game_tag_relation SET update_time = #{updateTime}, is_deleted = 1 WHERE id = #{id}")
    int delete(@Param("id") BigInteger id, @Param("updateTime") Integer updateTime);
} 