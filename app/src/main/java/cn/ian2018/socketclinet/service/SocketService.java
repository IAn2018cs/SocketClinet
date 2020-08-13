package cn.ian2018.socketclinet.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dhh.websocket.RxWebSocket;
import com.dhh.websocket.WebSocketSubscriber;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import cn.ian2018.socketclinet.db.RepositoryProvider;
import cn.ian2018.socketclinet.db.data.MsgInfo;
import cn.ian2018.socketclinet.event.ListUpdateEvent;
import cn.ian2018.socketclinet.event.ReceivedMsgEvent;
import cn.ian2018.socketclinet.modle.ChartRequest;
import cn.ian2018.socketclinet.modle.ConnectRequest;
import cn.ian2018.socketclinet.modle.InitRequest;
import cn.ian2018.socketclinet.modle.ReceiveSuccessRequest;
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
                if (TextUtils.isEmpty(otherPublicKey)) {
                    ConnectRequest connectRequest = new ConnectRequest("connect", otherUserId);
                    sendData(new Gson().toJson(connectRequest));
                }
                break;
            case 1002:
                String chart = intent.getStringExtra("chart");
                String aesKey = KeyUtil.generateAESKey();
                String encryptKey = KeyUtil.encryptData(otherPublicKey, aesKey.getBytes());
                String encryptMsg = KeyUtil.aesEncrypt(aesKey, (SPUtil.getId(SocketService.this) + ": " + chart).getBytes());
                ChartRequest chartRequest = new ChartRequest("chart", otherUserId, encryptKey, encryptMsg);
                sendData(new Gson().toJson(chartRequest));
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
        RxWebSocket.get("ws://111.229.253.137:9503")
                .subscribe(new WebSocketSubscriber() {
                    @Override
                    public void onOpen(@NonNull WebSocket webSocket) {
                        SocketService.this.webSocket = webSocket;
                        Log.d("MainActivity", "onOpen:");
                        InitRequest initRequest = new InitRequest("init", SPUtil.getId(SocketService.this), SPUtil.getPublicKey(SocketService.this));
                        sendData(new Gson().toJson(initRequest));
                    }

                    @Override
                    public void onMessage(@NonNull String text) {
                        Log.d("MainActivity", "返回数据:" + text);
                        try {
                            JSONObject jsonObject = new JSONObject(text);
                            String action = jsonObject.getString("action");
                            if (action.equals("connect")) {
                                otherPublicKey = jsonObject.getString("publicKey");
                            } else if (action.equals("chart")) {
                                String aesKey = jsonObject.getString("aesKey");
                                String msg = jsonObject.getString("msg");
                                long time = jsonObject.getLong("time");
                                long msgId = jsonObject.getLong("msgId");
                                String ip = jsonObject.getString("ip");
                                String key = new String(KeyUtil.decryptData(SPUtil.getPrivateKey(SocketService.this), aesKey));
                                String decryptMsg = new String(KeyUtil.aesDecrypt(key, msg));

                                MsgInfo msgInfo = new MsgInfo(decryptMsg, MsgInfo.TYPE_RECEIVED, ip, time);
                                EventBus.getDefault().post(new ReceivedMsgEvent(msgInfo));

                                RepositoryProvider.INSTANCE.providerMsgInfoRepository(SocketService.this).insertMsg(msgInfo);

                                ReceiveSuccessRequest successRequest = new ReceiveSuccessRequest("received", msgId);
                                sendData(new Gson().toJson(successRequest));
                            } else if (action.equals("list")) {
                                String list = jsonObject.getString("onlineList");
                                if (!TextUtils.isEmpty(list)) {
                                    SPUtil.userList = list;
                                    EventBus.getDefault().post(new ListUpdateEvent());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

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
