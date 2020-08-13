package cn.ian2018.socketclinet.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.ian2018.socketclinet.R;
import cn.ian2018.socketclinet.adapter.UserAdapter;
import cn.ian2018.socketclinet.event.ListUpdateEvent;
import cn.ian2018.socketclinet.util.KeyUtil;
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
                MainActivity.start(ChooseActivity.this, id);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);

        testa();
    }

    private void testa() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Pair<String, String> stringStringPair = KeyUtil.generateKey();
            list.add(stringStringPair.first);
        }
        long start = System.currentTimeMillis();
        String aesKey = KeyUtil.generateAESKey();
        Log.d("CHEN", "aes: " + aesKey);
        StringBuilder re = new StringBuilder();
        for (String s : list) {
            String encryptKey = KeyUtil.encryptData(s, aesKey.getBytes());
            Log.d("CHEN", "encryptKey: " + encryptKey);
            re.append(encryptKey).append("####");
        }
        Log.d("CHEN", "result: " + re.toString());
        Log.d("CHEN", "耗时：" + (System.currentTimeMillis() - start) / 1000f);
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
