package cn.ian2018.socketclinet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.ian2018.socketclinet.R
import cn.ian2018.socketclinet.api.E2eeApi
import cn.ian2018.socketclinet.util.SPUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.android.synthetic.main.activity_join_group.*

/**
 * Created by chenshuai on 2020/8/14
 */
class JoinGroupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_group)
    }

    fun join(view: View) {
        val code = codeEt.text.toString()
        CoroutineScope(Dispatchers.Main).launch {
            val joinGroup = E2eeApi.joinGroup(SPUtil.getId(this@JoinGroupActivity), code)
            joinGroup?.data?.let {
                Toast.makeText(this@JoinGroupActivity, "加入成功：${it.groupName}", Toast.LENGTH_LONG).show()
                finish()
                return@launch
            }
            Toast.makeText(this@JoinGroupActivity, "加入失败：${joinGroup?.message}", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, JoinGroupActivity::class.java)
            context.startActivity(intent)
        }
    }
}