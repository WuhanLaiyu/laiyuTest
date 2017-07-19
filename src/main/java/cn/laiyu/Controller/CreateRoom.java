package cn.laiyu.Controller;

import cn.laiyu.PoJo.Room.Room;
import cn.laiyu.PoJo.Room.RoomDTO;
import cn.laiyu.PoJo.User.User;
import cn.laiyu.Service.RoomService;
import cn.laiyu.WebSocket.RoomWebSocket;
import com.alibaba.fastjson.JSON;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by humac on 2017/6/27.
 */

@RestController
@RequestMapping("/room")
public class CreateRoom {
    @Resource
    private RoomService roomService;

    @RequestMapping("/create")
    public void createRoom(HttpServletRequest request, HttpServletResponse response) {
        String openId = request.getParameter("openId");
        String imagePath=request.getParameter("imagePath");
        User user = new User();
        user.setOpenId(openId);
        user.setImagePath(imagePath);

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setUserName(user.getOpenId());
        int roomId = this.roomService.addRoom(roomDTO);

        Room room = new Room(user, roomId);
        room.initRoom();
        RoomWebSocket.rooms.put(roomId, room);
        //返回一个roomId
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        Map resultMap = new HashMap<String, Object>();
        resultMap.put("roomId", roomId);
        resultMap.put("openId", openId);
        datas.add(resultMap);
        String jsonResult = JSON.toJSONString(datas);
        renderData(response, jsonResult);
    }


    private void renderData(HttpServletResponse response, String data) {
        PrintWriter printWriter = null;
        try {
            response.setContentType("text/html;charset=GBK");//解决中文乱码
            response.setCharacterEncoding("UTF-8");
            printWriter = response.getWriter();
            printWriter.print(data);
        } catch (IOException ex) {

        } finally {
            if (null != printWriter) {

                printWriter.flush();
                printWriter.close();
            }
        }
    }
}
