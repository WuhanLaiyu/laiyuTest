package cn.laiyu.PoJo.Room;

import cn.laiyu.LaiyudebugApplication;
import cn.laiyu.PoJo.Seat.SeatState;
import cn.laiyu.PoJo.User.PlayUser;
import cn.laiyu.PoJo.User.User;
import cn.laiyu.PoJo.User.Vote;
import cn.laiyu.PoJo.Vote.VoteSubject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by humac on 2017/6/26.
 */
public class Room {
    public Integer campiagnSeatId;

    public VoteSubject voteSubject =new VoteSubject();


    private User homeOwner;

    private int roomID;

    private CopyOnWriteArraySet<User> userSet = new CopyOnWriteArraySet<User>();

    private HashMap<User, Integer> restSet = new HashMap<User, Integer>();

    private HashMap<Integer, SeatState> playSet = new HashMap<Integer, SeatState>(16);

    public User getHomeOwner( ) {
        return homeOwner;
    }

    public void setHomeOwner(User homeOwner) {
        this.homeOwner = homeOwner;
    }

    public int getRoomID( ) {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public CopyOnWriteArraySet<User> getUserSet( ) {
        return this.userSet;
    }

    public Room(User homeOwner, int roomID) {
        this.homeOwner = homeOwner;
        this.roomID = roomID;
    }

    public HashMap<Integer, SeatState> getPlaySet( ) {
        return playSet;
    }

    public void setPlaySet(HashMap<Integer, SeatState> playSet) {
        this.playSet = playSet;
    }

    public HashMap<User, Integer> getRestSet( ) {
        return restSet;

    }


    /*
    * seatState:0 没有人坐
    *           1 有人坐
    *           -1 位置关闭
    * */
    public void initRoom( ) {
        for (int i = 1; i <= 12; i++) {
            SeatState seatState = new SeatState();

            seatState.seatState = 0;
            playSet.put(i, seatState);
        }
        for (int i = 13; i <= 16; i++) {
            SeatState seatState = new SeatState();
            seatState.seatState = -1;
            playSet.put(i, seatState);
        }
    }

    public void joinRoom(User user) {
        this.userSet.add(user);
        this.restSet.put(user, 0);
    }

    public int exitRoom(User user) {
        int flag = 0;
        this.userSet.remove(user);
        int f=0;
        Iterator<Map.Entry<Integer,SeatState>> itr=this.playSet.entrySet().iterator();
        while(itr.hasNext()){
            Map.Entry<Integer,SeatState> entry= itr.next();
            if(entry.getValue().playUser==null){
                continue;
            }
             if(user.getOpenId().equals(entry.getValue().playUser.getOpenId())){
                 f=1;
                 break;
             }
        }
        if (f==1) {
            Iterator<Map.Entry<Integer, SeatState>> it = this.playSet.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, SeatState> entry = (Map.Entry) it.next();
                if(entry.getValue().playUser==null){
                    continue;
                }
                if (user.getOpenId().equals(entry.getValue().playUser.getOpenId())) {
                    SeatState seatState=new SeatState();
                    seatState.seatState=0;
                    seatState.playUser=null;
                    this.playSet.put(entry.getKey(),seatState);
                    flag = 1;
                    break;
                }
            }
        } else {
            Iterator<Map.Entry<User, Integer>> it = this.restSet.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<User,Integer> entry=it.next();
                if(entry.getKey().getOpenId().equals(user.getOpenId())){
                    it.remove();
                    break;
                }
            }
            flag = 1;
        }

        return flag;
    }


    public void exitGame(String openId){
        Iterator<Map.Entry<Integer,SeatState>> it = this.playSet.entrySet().iterator();
        SeatState temp=new SeatState();
        Integer seadId=-1;
        while (it.hasNext()){
            Map.Entry entry = (Map.Entry) it.next();
            SeatState seatState=(SeatState) entry.getValue();
            if(seatState.playUser==null) {
                continue;
            }
            try {
                if(seatState.playUser.getOpenId().equals(openId)){
                    temp=seatState;
                    seadId=(Integer) entry.getKey();
                }
            }catch (Exception e){

            }
        }
        restSet.put((User)temp.playUser,0);
        temp.seatState=0;
        temp.playUser=null;
        playSet.put(seadId,temp);
        LaiyudebugApplication.logger.info(openId+"退出了房间"+roomID+"的游戏");

    }
    public synchronized void joinGame(Integer seatId, String openId) {
        SeatState seatState=this.playSet.get(seatId);
        if(seatState.seatState==1){
            return;
        }
        User temp=new User();
        Iterator<Map.Entry<User,Integer>> it = this.restSet.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<User, Integer> entry = (Map.Entry) it.next();
            User user=entry.getKey();

            if(user.getOpenId().equals(openId)){
                temp=user;
            }
        }

        PlayUser playUser=new PlayUser(seatId,temp);

        seatState.playUser=playUser;
        seatState.seatState=1;
        this.restSet.remove(temp);
        this.playSet.put(seatId,seatState);
        LaiyudebugApplication.logger.info(openId+"加入了房间"+roomID+"的游戏");

    }

}
