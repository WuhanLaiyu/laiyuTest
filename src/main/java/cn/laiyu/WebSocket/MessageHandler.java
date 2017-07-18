package cn.laiyu.WebSocket;

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
            case "109":
                MessageHandle((SeatStatusMessage)message,room);

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


    }

    private static void MessageHandle(RelayMessage message, Room room) throws IOException {
        GameBroadCast(room,JSONObject.toJSONString(message));
    }

    public  static void MessageHandle(BeginVoteMessage message,Room room) throws IOException {
        message.statusCode="106";
        GameBroadCast(room,JSONObject.toJSONString(message));
        CommonVoteQuiz commonVoteQuiz=new CommonVoteQuiz(15,room);
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
        message.statusCode="106";
        GameBroadCast(room,JSONObject.toJSONString(message));
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
    }


    public static void MessageHandle(ExitCamiagnMessage message,Room room) throws IOException {
        room.voteSubject.eixtCampaignObservers(message.seatId);
        CampiagnListResponseMessage camListResMes=new CampiagnListResponseMessage();
        camListResMes.campiagnList=room.voteSubject.getCampaignObservers();
        camListResMes.statusCode="104";
        camListResMes.ticTag=room.voteSubject.ticTag;
        GameBroadCast(room,JSONObject.toJSONString(camListResMes));
    }


    public static void MessageHandle(BeginCamiagnMessage message, Room room) throws IOException, InterruptedException {
        int limitSec=5;
        //开启竞选模式
        CampiagnResponseMessage responseC=new CampiagnResponseMessage();
        responseC.statusCode=103;
        responseC.windowStant=1;
        responseC.isCampiagnProcess=1;
        GameBroadCast(room,JSONObject.toJSONString(responseC));
        responseC.isCampiagnProcess=0;
        String resMessage=JSONObject.toJSONString(responseC);
        VoteTimeQuiz timeQuiz=new VoteTimeQuiz(5,resMessage,room);
        Thread thread=new Thread(timeQuiz);
        thread.start();
    }


    public static void MessageHandle(JoinCamiagnMessage message, Room room) throws IOException {
        room.voteSubject.addCampaignObservers(message.seatId);
        CampiagnListResponseMessage camListResMes=new CampiagnListResponseMessage();
        camListResMes.campiagnList=room.voteSubject.getCampaignObservers();
        camListResMes.ticTag=room.voteSubject.ticTag;
        camListResMes.statusCode="104";
        GameBroadCast(room,JSONObject.toJSONString(camListResMes));
    }

    private static void calculateVoteResult( ) {

    }

    public static void MessageHandle(JoinGameMessage message,Room room) throws IOException {
        room.joinGame(message.getSeatId(),message.getOpenId());
        String homeStructure = getHomeStructure(room);
        GameBroadCast(room,homeStructure);
    }
    public static void MessageHandle(RestGameMessage message,Room room) throws IOException {
        room.exitGame(message.getOpenId());
        String homeStructure = getHomeStructure(room);
        GameBroadCast(room,homeStructure);
    }
}
