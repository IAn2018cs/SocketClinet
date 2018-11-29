package cn.ian2018.socketclinet.modle;

/**
 * Created by Administrator on 2017/1/24/024.
 */

public class Msg {
    private String content;
    private int type;
    private String ip;
    private String time;
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SEND = 1;

    public Msg(String content, int type, String ip, String time) {
        this.content = content;
        this.type = type;
        this.ip = ip;
        this.time = time;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }
}
