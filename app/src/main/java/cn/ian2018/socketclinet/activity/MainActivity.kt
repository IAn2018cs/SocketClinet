package cn.ian2018.socketclinet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ian2018.socketclinet.R
import cn.ian2018.socketclinet.adapter.MsgAdapter
import cn.ian2018.socketclinet.db.RepositoryProvider
import cn.ian2018.socketclinet.db.data.MsgInfo
import cn.ian2018.socketclinet.db.repository.MsgInfoRepository
import cn.ian2018.socketclinet.event.ReceivedMsgEvent
import cn.ian2018.socketclinet.service.SocketService
import cn.ian2018.socketclinet.util.SPUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class MainActivity : AppCompatActivity() {

    private val mMsgList: MutableList<MsgInfo> = ArrayList()
    private var adatper: MsgAdapter = MsgAdapter(mMsgList)

    private var otherId: String? = null
    private lateinit var msgInfoRepository: MsgInfoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)

        otherId = intent.getStringExtra("other")

        savedInstanceState?.let { onRestoreInstanceState(it) }

        initView()

        msgInfoRepository = RepositoryProvider.providerMsgInfoRepository(this)
        CoroutineScope(Dispatchers.Main).launch {
            val allMsg = msgInfoRepository.getAllMsg()
            mMsgList.addAll(allMsg)
            adatper.notifyDataSetChanged()
            chart_recycler.scrollToPosition(mMsgList.size - 1)
        }
    }

    override fun onResume() {
        super.onResume()
        if (TextUtils.isEmpty(otherId)) {
            finish()
        } else {
            SocketService.connect(this, otherId)
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("other", otherId)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        otherId = savedInstanceState.getString("other")
        SocketService.connect(this, otherId)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ReceivedMsgEvent) {
        receivedMsg(event.msg)
    }

    private fun initView() {
        // 设置点击事件
        send.setOnClickListener {
            val data = msg.text.toString()
            if (data.isNotBlank()) {
                sendMsg(data)
                msg.setText("")
            }
        }

        // 设置recyclerView
        chart_recycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adatper
        }
    }


    private fun sendMsg(data: String) {
        SocketService.chart(this, data)
        val massage = MsgInfo(data, MsgInfo.TYPE_SEND, "", System.currentTimeMillis())
        mMsgList.add(massage)
        adatper.notifyItemChanged(mMsgList.size - 1)
        chart_recycler.scrollToPosition(mMsgList.size - 1)

        msgInfoRepository.insertMsg(massage)
    }

    private fun receivedMsg(data: MsgInfo) {
        mMsgList.add(data)
        adatper.notifyItemChanged(mMsgList.size - 1)
        chart_recycler!!.scrollToPosition(mMsgList.size - 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        SPUtil.userList = ""
    }

    companion object {

        @JvmStatic
        fun start(context: Context, other: String?) {
            val starter = Intent(context, MainActivity::class.java)
            starter.putExtra("other", other)
            starter.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(starter)
        }
    }
}