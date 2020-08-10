package cn.ian2018.socketclinet;

import android.app.Application;

import com.dhh.websocket.Config;
import com.dhh.websocket.RxWebSocket;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by chenshuai on 2020/8/6
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //init config 在使用RxWebSocket之前设置即可，推荐在application里初始化
        Config config = new Config.Builder()
                .setShowLog(true)           //show  log
                .setClient(new OkHttpClient.Builder()
                        .pingInterval(3, TimeUnit.SECONDS) // 设置心跳间隔，这个是3秒检测一次
                        .build())
                .setShowLog(true, "CHEN")
                .setReconnectInterval(2, TimeUnit.SECONDS)  //set reconnect interval
                .build();
        RxWebSocket.setConfig(config);
    }
}
