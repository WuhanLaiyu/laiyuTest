package cn.laiyu.PoJo.User;

import javax.websocket.Session;

/**
 * Created by humac on 2017/6/26.
 */
public class User {
    private String nickName;

    private String imagePath;

    private String openId;

    private Session session;

    public Session getSession( ) {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getOpenId( ) {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getNickName( ) {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getImagePath( ) {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
