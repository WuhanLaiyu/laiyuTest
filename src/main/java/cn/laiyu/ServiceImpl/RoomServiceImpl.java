package cn.laiyu.ServiceImpl;

import cn.laiyu.Mapper.RoomMapper;
import cn.laiyu.PoJo.Room.Room;
import cn.laiyu.PoJo.Room.RoomDTO;
import cn.laiyu.Service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by humac on 2017/6/27.
 */
@Service

public class RoomServiceImpl implements RoomService {
    @Autowired
    RoomMapper roomMapper;

    @Override
    public int addRoomFaker(RoomDTO roomDTO) {

        this.roomMapper.addRomeFaker(roomDTO);
        return roomDTO.getId();
    }

    @Override
    public int getMaxId() {
        return this.roomMapper.getMaxId();
    }

    @Override
    public RoomDTO getReleaseRoom( ) {
        return this.roomMapper.getReleaseRoom();
    }

    @Override
    public int addRoom(RoomDTO roomDTO) {
        this.roomMapper.addRome(roomDTO);
        return roomDTO.getId();
    }

    public static void main(String args[]) {
        RoomServiceImpl roomService = new RoomServiceImpl();
        System.out.println(roomService.getReleaseRoom());

    }

}


