package cn.laiyu.Message.ReponseMessage;

import cn.laiyu.PoJo.Seat.SeatState;
import cn.laiyu.PoJo.User.PlayUser;
import cn.laiyu.PoJo.User.User;
import cn.laiyu.PoJo.User.UserDTO;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by humac on 2017/6/28.
 */
public class RoomMessage {
    public int statusCode;

    public String homeOwner;

    public String imagePath;

    public ArrayList<UserDTO> restSet;

    public HashMap<String, UserDTO> playSet;

    public HashMap<String, Integer> seatStan;
}
