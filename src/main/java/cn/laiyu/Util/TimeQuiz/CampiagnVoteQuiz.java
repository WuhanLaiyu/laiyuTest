package cn.laiyu.Util.TimeQuiz;

import cn.laiyu.LaiyudebugApplication;
import cn.laiyu.Message.ReponseMessage.ResTimeMessage;
import cn.laiyu.Message.ReponseMessage.VoteResultResMessage;
import cn.laiyu.PoJo.Room.Room;
import cn.laiyu.PoJo.Seat.SeatState;
import cn.laiyu.PoJo.User.Vote;
import cn.laiyu.PoJo.Vote.VoteObserver;
import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static cn.laiyu.WebSocket.RoomWebSocket.GameBroadCast;

/**
 * Created by humac on 2017/7/11.
 */
public class CampiagnVoteQuiz implements Runnable{
    private int limitSec ;

    private Room  room;

    public  ConcurrentHashMap<String,ArrayList<String>> voteResult=new ConcurrentHashMap<String,ArrayList<String>>();

    public CampiagnVoteQuiz(int limitSec,Room room){
        this.limitSec=limitSec;
        this.room=room;
    }
    @Override
    public void run( ) {
        while(limitSec > 0){
            --limitSec;
            ResTimeMessage resTimeMessage=new ResTimeMessage();
            resTimeMessage.lastTime=limitSec;
            resTimeMessage.statusCode=131;
            try {
                GameBroadCast(room, JSON.toJSONString(resTimeMessage));}
            catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("remain sceconds:"+limitSec);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LaiyudebugApplication.logger.info(room.getRoomID()+"房间的警长竞选投票结束");

        
        List list=room.voteSubject.getVoteObservers();
        for (int i=0;i<list.size();i++){

            VoteObserver voteObserver=(VoteObserver)(list.get(i));
            System.out.println("tagetSeatId:"+voteObserver.tagetSeatId);
            System.out.println("mySeatId:"+voteObserver.mySeatId);
            ArrayList<String> tempArr=voteResult.get(voteObserver.tagetSeatId+"");
            if(tempArr==null){
                System.out.println("list is null");
                tempArr =new ArrayList<String>();
            }
//            if(tempArr==null){
//               System.out.println("null login!!!!");
//               tempArr=new ArrayList<String>();
//               tempArr.add(String.valueOf(voteObserver.mySeatId));
//               voteResult.put(voteObserver.tagetSeatId+"",tempArr);
//           }else{
//               System.out.println("not null!!!!");
//               voteResult.get(voteObserver.tagetSeatId).add(String.valueOf(voteObserver.mySeatId));
//           }
            tempArr.add(String.valueOf(voteObserver.mySeatId));
            voteResult.put(voteObserver.tagetSeatId+"",tempArr);
        }
        Iterator<Map.Entry<String,ArrayList<String>>> it = voteResult.entrySet().iterator();
        int flag=0;
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry) it.next();
            if(entry.getKey()!=null){
                if(entry.getKey().equals("0")){
                    continue;
                }
                List temp=(List)entry.getValue();
                int size =temp.size();
                if(size>flag){
                    flag=size;
                }
            }
        }
        Iterator<Map.Entry<String,ArrayList<String>>> it1 = voteResult.entrySet().iterator();
        CopyOnWriteArrayList<String> plaTic=new CopyOnWriteArrayList<String>();
        while(it1.hasNext()){
            Map.Entry entry = (Map.Entry) it1.next();
            List temp=(List)entry.getValue();
            if(entry.getKey().equals("0")){
                continue;
            }
            int size =temp.size();
            if(size==flag){
                plaTic.add(""+entry.getKey());
            }
        }
        VoteResultResMessage voteResultResMessage=new VoteResultResMessage();
        voteResultResMessage.ticTag=plaTic;
        voteResultResMessage.voteResult=voteResult;
        voteResultResMessage.statusCode="105";
        System.out.println(room.voteSubject.getCampaignObservers().size());
        if(plaTic.size()==1){
            room.campiagnSeatId=Integer.parseInt(plaTic.get(0));
        }
        String message= JSON.toJSONString(voteResultResMessage);
        try {
            GameBroadCast(room, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        voteResult.clear();
        room.voteSubject.getCampaignObservers().clear();
        room.voteSubject.getVoteObservers().clear();
    }

}
