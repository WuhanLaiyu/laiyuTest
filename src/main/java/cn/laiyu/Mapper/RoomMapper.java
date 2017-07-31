package cn.laiyu.Mapper;

import cn.laiyu.PoJo.Room.RoomDTO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

/**
 * Created by humac on 2017/6/27.
 */
@Component
@Mapper
public interface RoomMapper {
    @Select("select * from room where is_alive=0 order by rand() limit 1")
    @Results(id = "RoomDTO", value = {
            @Result(column = "Id", property = "id", javaType = Integer.class),
            @Result(column = "userName", property = "userName", javaType = String.class),
            @Result(column = "is_alive", property = "isAlive", javaType = Integer.class)
    })
    RoomDTO getReleaseRoom();

    @Select("select Id from room order by  Id desc limit 1")
    int getMaxId();

    @Insert("insert into room(openId,is_alive) values(#{RoomDTO.openId},1)")
    @Options(useGeneratedKeys = true, keyProperty = "RoomDTO.id")
    void addRome(@Param("RoomDTO") RoomDTO roomDTO);

    @Insert("insert into room(Id,openId,is_alive) values(#{RoomDTO.id},#{RoomDTO.openId},1)")
    @Options(useGeneratedKeys = true, keyProperty = "RoomDTO.id")
    void addRomeFaker(@Param("RoomDTO") RoomDTO roomDTO);
}
