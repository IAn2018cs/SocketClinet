package cn.ian2018.socketclinet.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.ian2018.socketclinet.R;
import cn.ian2018.socketclinet.adapter.MsgAdapter;
import cn.ian2018.socketclinet.modle.Msg;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private EditText editText;
    private Button button;
    private List<Msg> mMsgList = new ArrayList<>();
    private MsgAdapter adatper;
    private Socket socket;

    final private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                try {
                    int localPort = socket.getLocalPort();
                    String data = (String) msg.obj;
                    String s = new String(data.getBytes("GBK"), "utf-8");

                    Log.d("TAG", localPort + "   " + s);

                    String[] split = s.split("//");
                    // 如果是从本机发出 说明是发送方
                    if ((split[0].split(":")[1]).equals(localPort + "")) {
                        Msg massage = new Msg(split[1], Msg.TYPE_SEND, split[0], split[2]);
                        mMsgList.add(massage);
                    } else {
                        Msg massage = new Msg(split[1], Msg.TYPE_RECEIVED, split[0], split[2]);
                        mMsgList.add(massage);
                    }

                    // 刷新数据
                    adatper.notifyItemChanged(mMsgList.size() - 1);
                    recyclerView.scrollToPosition(mMsgList.size() - 1);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        start();
    }

    private void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("123.206.57.216", 10086);

                    InputStream inputStream = socket.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        String data = new String(buffer, 0, len);
                        // 发到主线程中 收到的数据
                        Message message = Message.obtain();
                        message.what = 1;
                        message.obj = data;
                        mHandler.sendMessage(message);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initView() {
        // 消息列表
        recyclerView = (RecyclerView) findViewById(R.id.chart_recycler);

        // 输入框
        editText = (EditText) findViewById(R.id.msg);

        // 发生按钮
        button = (Button) findViewById(R.id.send);
        // 设置点击事件
        button.setOnClickListener(this);

        // 设置recyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        // 为recycleView设置适配器
        adatper = new MsgAdapter(mMsgList);
        recyclerView.setAdapter(adatper);
    }

    // 点击事件
    @Override
    public void onClick(View v) {
        final String data = editText.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OutputStream outputStream = socket.getOutputStream();
                    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");    //设置日期格式
                    outputStream.write((socket.getLocalAddress().toString().substring(1) + ":" + socket.getLocalPort() + "//" + data + "//" + df.format(new Date())).getBytes("utf-8"));
                    outputStream.flush();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            editText.setText("");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
