package com.example.provider.mapper.game;


import com.example.common.entity.Game;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface GameMapper  {
    @Select("SELECT * FROM game WHERE id = #{id} AND is_deleted = 0")
    Game getById(BigInteger id);

    @Select("select * from game where id = #{id} ")
    Game extractById(BigInteger id);

    int insert(@Param("game") Game game);

    int update(@Param("game") Game game);

    @Update("update game set is_deleted = 1, update_time=#{time} wBhere id = #{id} limit 1")
    int delete(@Param("id") BigInteger id, @Param("time") Integer time);


    List<Game> getAll(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize , @Param("keyword") String keyword, @Param("typeId") BigInteger typeId, @Param("ids") String ids);

    @Select("select * from game where type_id = #{typeId}")
    List<Game> getAllGameByTypeId(@Param("typeId") BigInteger typeId);

    int getTotalCount(@Param("keyword") String keyword);

    @Select("SELECT id from game where type_id = #{type_id} and is_deleted = 0")
    List<BigInteger> isExistByTypeId(@Param("type_id") BigInteger type_id);

}
