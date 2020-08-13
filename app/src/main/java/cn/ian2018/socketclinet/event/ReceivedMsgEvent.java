package cn.ian2018.socketclinet.event;

import cn.ian2018.socketclinet.db.data.MsgInfo;

/**
 * Created by chenshuai on 2020/8/10
 */
public class ReceivedMsgEvent {
    public MsgInfo msg;

    public ReceivedMsgEvent(MsgInfo msg) {
        this.msg = msg;
    }
}
