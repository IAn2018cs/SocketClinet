package cn.ian2018.socketclinet.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.ian2018.socketclinet.R;
import cn.ian2018.socketclinet.adapter.MsgAdapter;
import cn.ian2018.socketclinet.event.ReceivedMsgEvent;
import cn.ian2018.socketclinet.modle.Msg;
import cn.ian2018.socketclinet.service.SocketService;
import cn.ian2018.socketclinet.util.SPUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static void start(Context context, String other) {
        Intent starter = new Intent(context, MainActivity.class);
        starter.putExtra("other", other);
        context.startActivity(starter);
    }

    private RecyclerView recyclerView;
    private EditText editText;
    private List<Msg> mMsgList = new ArrayList<>();
    private MsgAdapter adatper;

    private String otherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);

        otherId = getIntent().getStringExtra("other");

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        initView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("other", otherId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        otherId = savedInstanceState.getString("other");
        SocketService.connect(this, otherId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceivedMsgEvent event) {
        receivedMsg(event.msg);
    }

    private void initView() {
        // 消息列表
        recyclerView = findViewById(R.id.chart_recycler);

        // 输入框
        editText = findViewById(R.id.msg);

        // 发生按钮
        Button button = findViewById(R.id.send);
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
        sendMsg(data);
        editText.setText("");
    }

    private void sendMsg(String data) {
        SocketService.chart(this, data);

        Msg massage = new Msg(data, Msg.TYPE_SEND, "", "");
        mMsgList.add(massage);
        adatper.notifyItemChanged(mMsgList.size() - 1);
        recyclerView.scrollToPosition(mMsgList.size() - 1);
    }

    private void receivedMsg(String data) {
        Msg massage = new Msg(data, Msg.TYPE_RECEIVED, "", "");
        mMsgList.add(massage);
        adatper.notifyItemChanged(mMsgList.size() - 1);
        recyclerView.scrollToPosition(mMsgList.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SPUtil.userList = "";
    }
}
