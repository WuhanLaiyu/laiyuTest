package cn.laiyu.WebSocket;

import cn.laiyu.PoJo.Room.Room;
import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static cn.laiyu.WebSocket.RoomWebSocket.GameBroadCast;

/**
 * Created by humac on 2017/7/20.
 */
public class PingPongQuiz implements Runnable {
    ConcurrentHashMap<Integer, Room> rooms = new ConcurrentHashMap<Integer, Room>();

    @Override
    public void run() {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Iterator<Map.Entry<Integer, Room>> it = rooms.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Room> roomEntry = it.next();
                Room room=roomEntry.getValue();
                try {
                    System.out.println("0x100001 send");
                    GameBroadCast(room,"0x100001");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

