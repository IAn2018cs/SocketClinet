package cn.ian2018.socketclinet.event;

/**
 * Created by chenshuai on 2020/8/10
 */
public class ReceivedMsgEvent {
    public String msg;

    public ReceivedMsgEvent(String msg) {
        this.msg = msg;
    }
}
