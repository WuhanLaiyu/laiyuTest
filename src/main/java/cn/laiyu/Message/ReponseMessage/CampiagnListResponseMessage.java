package cn.laiyu.Message.ReponseMessage;

import cn.laiyu.Message.BaseMessage;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by humac on 2017/7/11.
 */
public class CampiagnListResponseMessage extends BaseMessage {
    public List<Integer> campiagnList=new CopyOnWriteArrayList<>();

    public List<Integer>  ticTag=new CopyOnWriteArrayList<>();
}
