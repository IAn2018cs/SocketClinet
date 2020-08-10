package cn.ian2018.socketclinet.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cn.ian2018.socketclinet.R;
import cn.ian2018.socketclinet.service.SocketService;
import cn.ian2018.socketclinet.util.KeyUtil;
import cn.ian2018.socketclinet.util.SPUtil;

/**
 * Created by chenshuai on 2020/8/7
 */
public class StartActivity extends AppCompatActivity {

    private EditText editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        editText = findViewById(R.id.et_id);
        if (!TextUtils.isEmpty(SPUtil.getId(this))) {
            SocketService.start(this);
            ChooseActivity.start(this);
            finish();
        }
    }

    public void register(View view) {
        String s = editText.getText().toString().trim();
        if (!TextUtils.isEmpty(s)) {
            Pair<String, String> keyPair = KeyUtil.generateKey();
            SPUtil.savePublicKey(this, keyPair.first);
            SPUtil.savePrivateKey(this, keyPair.second);

            SPUtil.saveId(this, s);
            SocketService.start(this);
            ChooseActivity.start(this);
            finish();
        }
    }
}
