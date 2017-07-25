package cn.laiyu.Controller;

import cn.laiyu.Controller.HttpUtil.AesCbcUtil;
import
        cn.laiyu.PoJo.Room.Room;
import cn.laiyu.PoJo.Room.RoomDTO;
import cn.laiyu.PoJo.User.User;
import cn.laiyu.Service.RoomService;
import cn.laiyu.WebSocket.RoomWebSocket;
import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;

import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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
        String headCount=request.getParameter("headCount");
        User user = new User();
        user.setOpenId(openId);
        user.setImagePath(imagePath);

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setUserName(user.getOpenId());
        int roomId = this.roomService.addRoom(roomDTO);

        Room room = new Room(user, roomId);
        room.initRoom(Integer.parseInt(headCount));
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


    @ResponseBody
    @RequestMapping(value = "/decodeUserInfo",method = RequestMethod.POST)
    public Map decodeUserInfo(String encryptedData,String iv,String code){
        System.out.println(code);
        Map map=new HashMap();
        if(code==null||code.length()==0){
            map.put("status",0);
            map.put("msg","code 不能为空.");
            return map;
        }

        String wxspAppid="wx7eaccc059592c75b";

        String wxspSecret="c722c93ac085d36b7bc7f16eb763bfda";

        String grant_type = "authorization_code";

        String params = "appid=" + wxspAppid + "&secret=" + wxspSecret + "&js_code=" + code + "&grant_type=" + grant_type;
        //发送请求

        String sr = cn.laiyu.Controller.HttpUtil.HttpRequest.sendGet("https://api.weixin.qq.com/sns/jscode2session", params);
        System.out.println(sr);
        //解析相应内容（转换成json对象）
        JSONObject json = JSONObject.parseObject(sr);
        //获取会话密钥（session_key）
        String session_key = json.get("session_key").toString();
        //用户的唯一标识（openid）
        String openid = (String) json.get("openid");

        //////////////// 2、对encryptedData加密数据进行AES解密 ////////////////
        try {
            String result = AesCbcUtil.decrypt(encryptedData, session_key, iv, "UTF-8");
            if (null != result && result.length() > 0) {
                map.put("status", 1);
                map.put("msg", "解密成功");
                JSONObject userInfoJSON = JSONObject.parseObject(result);
                Map userInfo = new HashMap();
                userInfo.put("openId", userInfoJSON.get("openId"));
                userInfo.put("nickName", userInfoJSON.get("nickName"));
                userInfo.put("gender", userInfoJSON.get("gender"));
                userInfo.put("city", userInfoJSON.get("city"));
                userInfo.put("province", userInfoJSON.get("province"));
                userInfo.put("country", userInfoJSON.get("country"));
                userInfo.put("avatarUrl", userInfoJSON.get("avatarUrl"));
                userInfo.put("unionId", userInfoJSON.get("unionId"));
                map.put("userInfo", userInfo);
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("status", 0);
        map.put("msg", "解密失败");
        return map;
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
