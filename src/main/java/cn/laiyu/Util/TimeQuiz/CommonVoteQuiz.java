package cn.laiyu.Util.TimeQuiz;

import cn.laiyu.LaiyudebugApplication;
import cn.laiyu.Message.BaseMessage;
import cn.laiyu.Message.ReponseMessage.ResTimeMessage;
import cn.laiyu.Message.ReponseMessage.VoteResultResMessage;
import cn.laiyu.PoJo.Room.Room;
import cn.laiyu.PoJo.Seat.SeatState;
import cn.laiyu.PoJo.User.PlayUser;
import cn.laiyu.PoJo.User.User;
import cn.laiyu.PoJo.Vote.IObserver;
import cn.laiyu.PoJo.Vote.VoteObserver;
import cn.laiyu.WebSocket.RoomWebSocket;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.glassfish.external.statistics.Stats;
import com.sun.xml.internal.fastinfoset.util.StringArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static cn.laiyu.WebSocket.RoomWebSocket.GameBroadCast;
import static cn.laiyu.WebSocket.RoomWebSocket.oneBroadCast;

/**
 * Created by Administrator on 2017/7/17.
 */
public class CommonVoteQuiz extends  VoteQuiz implements  Runnable{
    private int limitSec;
    private Room room;
    private ConcurrentHashMap<String,ArrayList<String>> voteResult=new ConcurrentHashMap<>();

    public CommonVoteQuiz(int limitsec,Room room){
        this.limitSec=limitsec;
        this.room=room;
    }

    @Override
    public void run() {
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
        LaiyudebugApplication.logger.info(room.getRoomID()+"房间的投票结束");
        List voteObServers=room.voteSubject.getVoteObservers();
        for (int i=0;i<voteObServers.size();i++){
            VoteObserver voteObserve=(VoteObserver) voteObServers.get(i);
            ArrayList<String> list=voteResult.get(voteObserve.tagetSeatId+"");
            if(list==null){
                list=new ArrayList<>();
            }
            list.add(voteObserve.mySeatId+"");
            voteResult.put(voteObserve.tagetSeatId+"",list);
        }

        Iterator<Map.Entry<String,ArrayList<String>>> it=voteResult.entrySet().iterator();
        double flag=0;
        while(it.hasNext()){
            Map.Entry<String,ArrayList<String>> entry=it.next();
            if(entry.getKey().equals("0")){
                continue;
            }
            if(entry.getKey()!=null){
                List temp=(List)entry.getValue();
                double size =temp.size();
                if(temp.contains(room.campiagnSeatId+"")){
                   size+=0.5;
                }
                if(size>flag){
                    flag=size;
                }
            }
        }

        Iterator<Map.Entry<String,ArrayList<String>>> it1=voteResult.entrySet().iterator();
        CopyOnWriteArrayList<String> platic=new CopyOnWriteArrayList<>();
        while(it1.hasNext()){
            Map.Entry<String,ArrayList<String>> entyr=it1.next();
            List temp=(List)entyr.getValue();
            double size =temp.size();
            if(entyr.getKey().equals("0")){
                continue;
            }
            if(temp.contains(room.campiagnSeatId+"")){
                size+=0.5;
            }
            if(flag==size){
                platic.add(entyr.getKey());
            }
        }
        VoteResultResMessage message=new VoteResultResMessage();
        message.ticTag=platic;
        message.voteResult=voteResult;
        message.statusCode="105";


        try {
            GameBroadCast(room,JSON.toJSONString(message));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(platic.size()==1){
            SeatState state=room.getPlaySet().get(Integer.parseInt(platic.get(0)));//可能存在弃票人数最多的情况
            if(state!=null) {
                PlayUser playUser = state.playUser;
                state.playUser = null;
                state.seatState = -1;
                room.getPlaySet().put(Integer.parseInt(platic.get(0)), state);
                User user = (User) playUser;
                room.getRestSet().put(user, 0);
                String message1 = RoomWebSocket.getHomeStructure(room);
                try {
                    GameBroadCast(room, message1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BaseMessage message2 = new BaseMessage();
                message2.statusCode = "111";
                try {
                    oneBroadCast(room, JSONObject.toJSONString(message2), playUser.getOpenId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        voteResult.clear();
        room.voteSubject.getVoteObservers().clear();
        room.voteSubject.getCampaignObservers().clear();
    }
}
