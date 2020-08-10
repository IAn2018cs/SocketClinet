package cn.ian2018.socketclinet.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;

import cn.ian2018.socketclinet.R;
import cn.ian2018.socketclinet.adapter.UserAdapter;
import cn.ian2018.socketclinet.event.ListUpdateEvent;
import cn.ian2018.socketclinet.service.SocketService;
import cn.ian2018.socketclinet.util.SPUtil;

/**
 * Created by chenshuai on 2020/8/7
 */
public class ChooseActivity extends AppCompatActivity {

    private UserAdapter userAdapter;

    public static void start(Context context) {
        Intent starter = new Intent(context, ChooseActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        EventBus.getDefault().register(this);

        String list = SPUtil.userList;
        if (TextUtils.isEmpty(list)) {
            userAdapter = new UserAdapter(this);
        } else {
            String[] split = list.split(",");
            userAdapter = new UserAdapter(this, Arrays.asList(split));
        }
        userAdapter.setOnItemClick(new UserAdapter.OnItemClick() {
            @Override
            public void onItemClink(String id) {
                SocketService.connect(ChooseActivity.this, id);
                MainActivity.start(ChooseActivity.this, id);
                finish();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ListUpdateEvent event) {
        String list = SPUtil.userList;
        if (!TextUtils.isEmpty(list)) {
            String[] split = list.split(",");
            if (userAdapter != null) {
                userAdapter.setList(Arrays.asList(split));
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
