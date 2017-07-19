package cn.laiyu.Message.RequestMessage;

import cn.laiyu.Message.BaseMessage;

/**
 * Created by Administrator on 2017/7/18.
 */
public class ChangePoliceMessage extends BaseMessage{
    private Integer seatId;

    public Integer getSeatId() {
        return seatId;
    }

    public void setSeatId(Integer seatId) {
        this.seatId = seatId;
    }
}
