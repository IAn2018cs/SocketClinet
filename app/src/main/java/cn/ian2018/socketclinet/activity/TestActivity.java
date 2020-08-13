package cn.ian2018.socketclinet.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cn.ian2018.socketclinet.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by chenshuai on 2020/8/11
 */
public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void test(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 1_000; i++) {
                        OkHttpClient client = new OkHttpClient();
                        client.newWebSocket(new Request.Builder().get().url("ws://62.234.115.66:9503").build(), new WebSocketListener() {
                            @Override
                            public void onOpen(WebSocket webSocket, Response response) {
                                super.onOpen(webSocket, response);
                                Log.d("CHEN", "onOpen: " + response.toString());
                            }

                            @Override
                            public void onMessage(WebSocket webSocket, String text) {
                                super.onMessage(webSocket, text);
                                Log.d("CHEN", "onMessage: " + text);
                            }

                            @Override
                            public void onMessage(WebSocket webSocket, ByteString bytes) {
                                super.onMessage(webSocket, bytes);
                            }

                            @Override
                            public void onClosing(WebSocket webSocket, int code, String reason) {
                                super.onClosing(webSocket, code, reason);
                                Log.e("CHEN", "onClosing: " + reason);
                            }

                            @Override
                            public void onClosed(WebSocket webSocket, int code, String reason) {
                                super.onClosed(webSocket, code, reason);
                                Log.e("CHEN", "onClosed: " + reason);
                            }

                            @Override
                            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                                super.onFailure(webSocket, t, response);
                                if (response != null) {
                                    Log.e("CHEN", "onFailure: " + response.toString());
                                } else if (t != null){
                                    Log.e("CHEN", "onFailure: " + t.getMessage());
                                } else {
                                    Log.e("CHEN", "onFailure: ");
                                }
                            }
                        });

                        Thread.sleep(50);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("CHEN", "Exception: " + e.getMessage());
                }
            }
        }).start();
    }
}
