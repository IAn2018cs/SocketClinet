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
import kotlinx.android.synthetic.main.activity_create_group.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by chenshuai on 2020/8/14
 */
class CreateGroupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

    }

    fun create(view: View) {
        val name = nameEt.text.toString()
        CoroutineScope(Dispatchers.Main).launch {
            val createGroup = E2eeApi.createGroup(SPUtil.getId(this@CreateGroupActivity), name)
            createGroup?.data?.let {
                Toast.makeText(this@CreateGroupActivity, "创建成功：shareCode: ${it.shareCode}", Toast.LENGTH_LONG).show()
                finish()
                return@launch
            }
            Toast.makeText(this@CreateGroupActivity, "创建失败", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, CreateGroupActivity::class.java)
            context.startActivity(intent)
        }
    }
}