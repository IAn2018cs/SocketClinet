package cn.ian2018.socketclinet.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dhh.websocket.RxWebSocket;
import com.dhh.websocket.WebSocketSubscriber;

import org.greenrobot.eventbus.EventBus;

import cn.ian2018.socketclinet.event.ListUpdateEvent;
import cn.ian2018.socketclinet.event.ReceivedMsgEvent;
import cn.ian2018.socketclinet.util.KeyUtil;
import cn.ian2018.socketclinet.util.SPUtil;
import okhttp3.WebSocket;
import okio.ByteString;

/**
 * Created by chenshuai on 2020/8/7
 */
public class SocketService extends Service {

    public static void start(Context context) {
        Intent intent = new Intent(context, SocketService.class);
        context.startService(intent);
    }

    public static void connect(Context context, String otherId) {
        Intent intent = new Intent(context, SocketService.class);
        intent.putExtra("command", 1001);
        intent.putExtra("other", otherId);
        context.startService(intent);
    }

    public static void chart(Context context, String chart) {
        Intent intent = new Intent(context, SocketService.class);
        intent.putExtra("command", 1002);
        intent.putExtra("chart", chart);
        context.startService(intent);
    }

    private WebSocket webSocket;

    private String otherPublicKey;
    private String otherUserId;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        start();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        if (intent == null) {
            return result;
        }
        int command = intent.getIntExtra("command", -1);
        switch (command) {
            case 1001:
                otherUserId = intent.getStringExtra("other");
                sendData("connect#" + otherUserId);
                break;
            case 1002:
                String chart = intent.getStringExtra("chart");
                String aesKey = KeyUtil.generateAESKey();
                String encryptKey = KeyUtil.encryptData(otherPublicKey, aesKey.getBytes());
                String encryptMsg = KeyUtil.aesEncrypt(aesKey, (SPUtil.getId(SocketService.this) + ": " + chart).getBytes());
                String msg = "chart#" + otherUserId + "#" + encryptKey + "#" + encryptMsg;
                sendData(msg);
                break;
        }

        return result;
    }

    private void sendData(String data) {
        //url 对应的WebSocket 必须打开,否则报错
        if (webSocket != null) {
            webSocket.send(data);
        }
    }

    private void start() {
        RxWebSocket.get("ws://192.168.30.155:9503")
                .subscribe(new WebSocketSubscriber() {
                    @Override
                    public void onOpen(@NonNull WebSocket webSocket) {
                        SocketService.this.webSocket = webSocket;
                        Log.d("MainActivity", "onOpen:");
                        sendData("init#" + SPUtil.getId(SocketService.this) + "#" + SPUtil.getPublicKey(SocketService.this));
                    }

                    @Override
                    public void onMessage(@NonNull String text) {
                        Log.d("MainActivity", "返回数据:" + text);
                        String[] split = text.split("#");
                        String action = split[0];
                        if (action.equals("connect")) {
                            otherPublicKey = split[1];
                        } else if (action.equals("chart")) {
                            String aesKey = split[1];
                            String msg = split[2];
                            String key = new String(KeyUtil.decryptData(SPUtil.getPrivateKey(SocketService.this), aesKey));
                            EventBus.getDefault().post(new ReceivedMsgEvent(new String(KeyUtil.aesDecrypt(key, msg))));
                        } else if (action.equals("list")) {
                            if (split.length > 1) {
                                SPUtil.userList = split[1];
                                EventBus.getDefault().post(new ListUpdateEvent());
                            }
                        }
                    }

                    @Override
                    public void onMessage(@NonNull ByteString byteString) {

                    }

                    @Override
                    protected void onReconnect() {
                        Log.d("MainActivity", "重连:");
                    }

                    @Override
                    protected void onClose() {
                        Log.d("MainActivity", "onClose:");
                    }
                });
    }
}
