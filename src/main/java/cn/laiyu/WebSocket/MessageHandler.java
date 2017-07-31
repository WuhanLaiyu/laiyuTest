package cn.laiyu.WebSocket;

import cn.laiyu.LaiyudebugApplication;
import cn.laiyu.Message.BaseMessage;

import cn.laiyu.Message.ReponseMessage.CampiagnListResponseMessage;
import cn.laiyu.Message.ReponseMessage.CampiagnMessage;
import cn.laiyu.Message.ReponseMessage.CampiagnResponseMessage;

import cn.laiyu.Message.RequestMessage.*;
import cn.laiyu.PoJo.Room.Room;
import cn.laiyu.PoJo.User.Vote;
import cn.laiyu.PoJo.Vote.VoteObserver;
import cn.laiyu.PoJo.Vote.VoteSubject;
import cn.laiyu.Util.TimeQuiz.CampiagnVoteQuiz;
import cn.laiyu.Util.TimeQuiz.CommonVoteQuiz;
import cn.laiyu.Util.TimeQuiz.VoteTimeQuiz;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;

import static cn.laiyu.WebSocket.RoomWebSocket.GameBroadCast;
import static cn.laiyu.WebSocket.RoomWebSocket.getHomeStructure;



/**
 * Created by humac on 2017/7/5.
 */
public class MessageHandler {
    public static void MessageControl(BaseMessage message,Room room) throws IOException, InterruptedException {
        System.out.println(message);
        String classType=message.getClass().toString();
        BaseMessage messageTemp=null;
        switch (message.statusCode){
            case "211" :
                MessageHandle((JoinGameMessage)message,room);
                break;
            case "212" :
                MessageHandle((RestGameMessage)message,room);
                break;
            case "113" :
                MessageHandle((BeginCamiagnMessage)message,room);
                break;
            case "114" :
                MessageHandle((JoinCamiagnMessage)message,room);
                break;
            case "115" :
                MessageHandle((ExitCamiagnMessage)message,room);
                break;
            case "116" :
                MessageHandle((CampiagnVoteMessage)message,room);
                break;
            case "117" :
                MessageHandle((BeginCamiagnVoteMessage)message,room);
                break;
            case "118" :
                MessageHandle((BeginVoteMessage) message,room);
                break;
            case "120":
                MessageHandle((RelayMessage)message,room);
                break;
            case "141":
                MessageHandle((RelayMessage)message,room);
                break;
            case "142":
                MessageHandle((ReplayMessage)message,room);
                break;
            case "109":
                MessageHandle((SeatStatusMessage)message,room);
                break;
            case "110":
                MessageHandle((ChangePoliceMessage)message,room);
                break;
            case "112":
                MessageHandle((PoloceRestMessage)message,room);
                break;
            case "150":
                MessageHandle((RelayMessage)message,room);
                break;
        }
    }

    private static void MessageHandle(PoloceRestMessage message, Room room) {
        room.campiagnSeatId=null;
        try {
            GameBroadCast(room,JSONObject.toJSONString(message));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void MessageHandle(ChangePoliceMessage message, Room room) {
        room.campiagnSeatId=message.getSeatId();
        try {
            GameBroadCast(room,JSONObject.toJSONString(message));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void MessageHandle(SeatStatusMessage message, Room room){
        int seatState=room.getPlaySet().get(message.getSeatId()).seatState;
        if(seatState==0){
            room.getPlaySet().get(message.getSeatId()).seatState=-1;
        }else{
            room.getPlaySet().get(message.getSeatId()).seatState=0;
        }
        String message1=RoomWebSocket.getHomeStructure(room);
        try {
            GameBroadCast(room,message1);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void MessageHandle(ReplayMessage message, Room room) {
        room.voteSubject=new VoteSubject();
        room.campiagnSeatId=null;
        try {
            GameBroadCast(room,JSONObject.toJSONString(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void MessageHandle(RelayMessage message, Room room) throws IOException {
        GameBroadCast(room,JSONObject.toJSONString(message));
    }

    public  static void MessageHandle(BeginVoteMessage message,Room room) throws IOException {
        message.statusCode="106";
        GameBroadCast(room,JSONObject.toJSONString(message));
        LaiyudebugApplication.logger.info(room.getRoomID()+"房间开始投票");
        CommonVoteQuiz commonVoteQuiz=new CommonVoteQuiz(10,room);
        Thread thread=new Thread(commonVoteQuiz);
        thread.start();
    }

    public static void MessageHandle(BeginCamiagnVoteMessage message,Room room) throws IOException {
        System.out.println("recived message to string"+JSONObject.toJSONString(message));
        if(room.voteSubject.getCampaignObservers().size()==1){
            Integer seatId=room.voteSubject.getCampaignObservers().get(0);
            room.campiagnSeatId=seatId;
            CampiagnMessage message1=new CampiagnMessage();
            message1.campiagnSeatId=seatId;
            message1.statusCode="140";
            GameBroadCast(room,JSONObject.toJSONString(message1));
            return;
        }
        if(room.voteSubject.getCampaignObservers().size()==0){
            CampiagnMessage message1=new CampiagnMessage();
            message1.campiagnSeatId=0;
            message1.statusCode="140";
            GameBroadCast (room,JSONObject.toJSONString(message1));
            return;
        }
        message.statusCode="106";
        GameBroadCast(room,JSONObject.toJSONString(message));
        LaiyudebugApplication.logger.info(room.getRoomID()+"房间警长投票开始");
        CampiagnVoteQuiz campiagnVoteQuiz=new CampiagnVoteQuiz(10,room);
        Thread thread=new Thread(campiagnVoteQuiz);
        thread.start();
    }

    public static void MessageHandle(CampiagnVoteMessage message, Room room){
        VoteObserver voter=new VoteObserver();
        System.out.println(message.getTargetId()+"   ____"+message.getSeatId());
        voter.tagetSeatId=message.getTargetId();
        voter.mySeatId=message.getSeatId();
        System.out.println( voter.tagetSeatId);
        room.voteSubject.addObservers(voter);
        LaiyudebugApplication.logger.info(room.getRoomID()+"房间警长投票中"+message.getSeatId()+"号玩家投票"+message.getTargetId()+"号玩家");
    }


    public static void MessageHandle(ExitCamiagnMessage message,Room room) throws IOException {
        room.voteSubject.eixtCampaignObservers(message.seatId);
        LaiyudebugApplication.logger.info(room.getRoomID()+"房间的"+message.seatId+"号玩家退出了警长竞选");
        CampiagnListResponseMessage camListResMes=new CampiagnListResponseMessage();
        for(Integer str:room.voteSubject.getCampaignObservers()){
            camListResMes.campiagnList.add(String.valueOf(str));
        }
        camListResMes.statusCode="104";
        camListResMes.ticTag=room.voteSubject.ticTag;
        camListResMes.water=message.seatId+"";
        GameBroadCast(room,JSONObject.toJSONString(camListResMes));
    }

    public static void MessageHandle(BaseMessage message,Room room) throws IOException {
        GameBroadCast(room,JSONObject.toJSONString(message));
    }

    public static void MessageHandle(BeginCamiagnMessage message, Room room) throws IOException, InterruptedException {
        int limitSec=5;
        //开启竞选模式
        CampiagnResponseMessage responseC=new CampiagnResponseMessage();
        responseC.statusCode=103;
        responseC.windowStant=1;
        responseC.isCampiagnProcess=1;
        GameBroadCast(room,JSONObject.toJSONString(responseC));
        LaiyudebugApplication.logger.info("房间"+room.getRoomID()+"开始了警长竞选");
        responseC.isCampiagnProcess=0;
        String resMessage=JSONObject.toJSONString(responseC);
        VoteTimeQuiz timeQuiz=new VoteTimeQuiz(5,resMessage,room);
        Thread thread=new Thread(timeQuiz);
        thread.start();
    }


    public static void MessageHandle(JoinCamiagnMessage message, Room room) throws IOException {
        room.voteSubject.addCampaignObservers(message.seatId);
        LaiyudebugApplication.logger.info(room.getRoomID()+"房间的"+message.seatId+"号玩家加入了警长竞选");
        CampiagnListResponseMessage camListResMes=new CampiagnListResponseMessage();
        for(Integer str:room.voteSubject.getCampaignObservers()){
            camListResMes.campiagnList.add(String.valueOf(str));
        }
        camListResMes.ticTag=room.voteSubject.ticTag;
        camListResMes.statusCode="104";
        GameBroadCast(room,JSONObject.toJSONString(camListResMes));
    }

    private static void calculateVoteResult( ) {

    }

    public static void MessageHandle(JoinGameMessage message,Room room) throws IOException {
        room.joinGame(message.getSeatId(),message.getOpenId() ,message.getStartSeatId());
        String homeStructure = getHomeStructure(room);
        GameBroadCast(room,homeStructure);
    }
    public static void MessageHandle(RestGameMessage message,Room room) throws IOException {
        room.exitGame(message.getOpenId());
        String homeStructure = getHomeStructure(room);
        GameBroadCast(room,homeStructure);
    }
}
