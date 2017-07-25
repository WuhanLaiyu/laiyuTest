package cn.laiyu.WebSocket;

import cn.laiyu.LaiyudebugApplication;
import cn.laiyu.Message.BaseMessage;
import cn.laiyu.Message.ReponseMessage.RoomMessage;
import cn.laiyu.PoJo.Room.Room;
import cn.laiyu.PoJo.Seat.SeatState;
import cn.laiyu.PoJo.User.PlayUser;
import cn.laiyu.PoJo.User.User;
import cn.laiyu.PoJo.User.UserDTO;
import cn.laiyu.Util.Encoder.MessageDecoder;
import cn.laiyu.Util.Encoder.MessageEncoder;

import cn.laiyu.Util.ParamDetermine.MapToBean;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by humac on 2017/6/26.
 */
@ServerEndpoint(value = "/room/{roomId}",
        decoders = {MessageDecoder.class},
        encoders = {MessageEncoder.class})
@Component
public class RoomWebSocket {

    private static int onlineCount = 0;

    public static ConcurrentHashMap<Session, User> sessionMap = new ConcurrentHashMap<Session, User>();

    public static ConcurrentHashMap<Integer, Room> rooms = new ConcurrentHashMap<Integer, Room>();

    private Session session;

    /*
    *   需要的参数：openId，nickName，imagePath，roomId
    * */
    static {
        User user = new User();
        user.setOpenId("asdasdasd");
        user.setNickName("asdasdas");
        user.setImagePath("sadasdas");
        Room room = new Room(user, 10001);
        room.initRoom(12);
        SeatState seatState = new SeatState();
        PlayUser user1 = new PlayUser(1, user);
        seatState.seatState = 1;
        seatState.playUser = user1;
        room.getPlaySet().put(1, seatState);
        rooms.put(10001, room);
    }
    static {
        PingPongQuiz pingPongQuiz=new PingPongQuiz();
        pingPongQuiz.rooms=rooms;
        Thread thread=new Thread(pingPongQuiz);
        thread.start();
    }
    @OnOpen
    public void onOpen(@PathParam("roomId") int roomId, Session session) throws Exception {
        System.out.println(session);

        this.session = session;
        String qryString = session.getQueryString();// 获取上传的参数
        String decodeStr = URLDecoder.decode(qryString, "utf-8");
        String[] params = decodeStr.split("&");
        Map<String, Object> param = cn.laiyu.Util.ParamDetermine.UrlDetermine.getInstance().DetermineUrl(decodeStr);
        Room myRoom = rooms.get(roomId);
        if (myRoom == null) {
            roomUnExitErrorBroadCast(session);
        } else {
            User user = (User) MapToBean.mapToBean(param, User.class);
            user.setSession(session);
            sessionMap.put(session, user);
            myRoom.joinRoom(user);
            String message = getHomeStructure(myRoom);
            GameBroadCast(myRoom,message);
            LaiyudebugApplication.logger.info(user.getOpenId()+"加入了房间"+roomId);
        }

    }

    @OnClose
    public void onClose(@PathParam("roomId") int roomId, Session session) throws IOException {
        User user = this.sessionMap.get(session);
        Room myRoom = rooms.get(roomId);
        myRoom.exitRoom(user);
        String message = getHomeStructure(myRoom);
        GameBroadCast(myRoom,message);
        LaiyudebugApplication.logger.info(user.getOpenId()+"退出了房间"+roomId);
    }

    @OnMessage
    public void onMessage(@PathParam("roomId") int roomId, BaseMessage message) throws IOException, InterruptedException {
        System.out.println("onclick start!!");
        Room myRoom =rooms.get(roomId);
        MessageHandler.MessageControl(message,myRoom);

    }



    public static String getHomeStructure(Room myRoom) {
        RoomMessage roomMessage = new RoomMessage();
        roomMessage.statusCode = 201;
        roomMessage.homeOwner = myRoom.getHomeOwner().getOpenId();
        roomMessage.imagePath=myRoom.getHomeOwner().getImagePath();

        ArrayList<UserDTO> restSet = new ArrayList<>();
        Iterator<Map.Entry<User, Integer>> iter = myRoom.getRestSet().entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            User tempUser = (User) entry.getKey();
            UserDTO userDTO = new UserDTO();
            userDTO.openId = tempUser.getOpenId();
            userDTO.imagePath = tempUser.getImagePath();
            userDTO.nickName = tempUser.getNickName();
            restSet.add(userDTO);
        }
        roomMessage.restSet = restSet;

        HashMap<String, UserDTO> playSet = new HashMap<>();
        HashMap<String, Integer> seatSet = new HashMap<>();
        Iterator<Map.Entry<Integer, SeatState>> it = myRoom.getPlaySet().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Integer seatId = (Integer) entry.getKey();
            SeatState seatState = (SeatState) entry.getValue();
            UserDTO user = new UserDTO();
            seatSet.put(seatId+"",seatState.seatState);

            if(seatState.playUser==null) {
                continue;
            }
            user.openId=seatState.playUser.getOpenId();
            user.imagePath=seatState.playUser.getImagePath();
            user.nickName=seatState.playUser.getNickName();
            playSet.put(seatId+"", user);
        }

        roomMessage.seatStan = seatSet;
        roomMessage.playSet = playSet;
        String message = JSON.toJSONString(roomMessage);
        return message;
    }

    public static void GameBroadCast(Room myRoom,String message) throws IOException {
        for (User item :
                myRoom.getUserSet()) {
            item.getSession().getBasicRemote().sendText(message);
        }

    }

    public static void oneBroadCast(Room myRoom,String message,String openId) throws IOException {
        for(User item:myRoom.getUserSet()){
            if(item.getOpenId().equals(openId)){
                item.getSession().getBasicRemote().sendText(message);
            }
        }
    }

    public static void roomUnExitErrorBroadCast(Session session) throws IOException {
        Map<Object, Object> roomErrorMessage = new HashMap<Object, Object>();
        roomErrorMessage.put("statusCode", 401);
        roomErrorMessage.put("message", "所输入房间号不存在！");
        String message = JSON.toJSONString(roomErrorMessage);
        session.getBasicRemote().sendText(message);
    }
}
