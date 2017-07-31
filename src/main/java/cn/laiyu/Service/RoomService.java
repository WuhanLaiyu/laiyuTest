package cn.laiyu.Service;

import cn.laiyu.PoJo.Room.Room;
import cn.laiyu.PoJo.Room.RoomDTO;

/**
 * Created by humac on 2017/6/27.
 */
public interface RoomService {
    RoomDTO getReleaseRoom( );

    int addRoom(RoomDTO roomDTO);

    int getMaxId();

    int addRoomFaker(RoomDTO roomDTO);
}
