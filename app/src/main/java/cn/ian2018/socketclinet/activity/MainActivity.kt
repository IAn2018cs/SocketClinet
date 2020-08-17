package cn.ian2018.socketclinet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ian2018.socketclinet.R
import cn.ian2018.socketclinet.adapter.MsgAdapter
import cn.ian2018.socketclinet.api.bean.GroupData
import cn.ian2018.socketclinet.api.bean.UserData
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

    private var type = -1

    private lateinit var userData: UserData
    private lateinit var groupData: GroupData

    private lateinit var msgInfoRepository: MsgInfoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)

        msgInfoRepository = RepositoryProvider.providerMsgInfoRepository(this)

        type = intent.getIntExtra("type", -1)

        if (type == 1) {
            userData = intent.getParcelableExtra("userData")
        } else {
            groupData = intent.getParcelableExtra("groupData")
        }

        initView()

        CoroutineScope(Dispatchers.Main).launch {
            val allMsg = if (type == 1) {
                msgInfoRepository.getAllMsg(userData.userId)
            } else {
                msgInfoRepository.getGroupMsg(groupData.groupId)
            }
            mMsgList.addAll(allMsg)
            adatper.notifyDataSetChanged()
            chart_recycler.scrollToPosition(mMsgList.size - 1)
        }
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
        val massage = if (type == 1) {
            SocketService.chart(this, data, userData.userId, userData.publicKey)
            MsgInfo(data, MsgInfo.TYPE_SEND, "", System.currentTimeMillis(), fromId = SPUtil.getId(this))
        } else {
            SocketService.chartGroup(this, groupData.groupId, data, groupData.members)
            MsgInfo(data, MsgInfo.TYPE_SEND, "", System.currentTimeMillis(), groupId = groupData.groupId, fromId = SPUtil.getId(this))
        }
        mMsgList.add(massage)
        adatper.notifyItemChanged(mMsgList.size - 1)
        chart_recycler.scrollToPosition(mMsgList.size - 1)

        msgInfoRepository.insertMsg(massage)
    }

    private fun receivedMsg(data: MsgInfo) {
        if (type == 1) {
            if (data.groupId == -1 && data.fromId == userData.userId) {
                mMsgList.add(data)
                adatper.notifyItemChanged(mMsgList.size - 1)
                chart_recycler!!.scrollToPosition(mMsgList.size - 1)
            }
        } else {
            if (data.groupId == groupData.groupId) {
                mMsgList.add(data)
                adatper.notifyItemChanged(mMsgList.size - 1)
                chart_recycler!!.scrollToPosition(mMsgList.size - 1)
            }
        }
    }

    companion object {
        fun start(context: Context, userData: UserData) {
            val starter = Intent(context, MainActivity::class.java)
            starter.putExtra("type", 1)
            starter.putExtra("userData", userData)
            starter.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(starter)
        }

        fun start(context: Context, groupData: GroupData) {
            val starter = Intent(context, MainActivity::class.java)
            starter.putExtra("type", 2)
            starter.putExtra("groupData", groupData)
            starter.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(starter)
        }
    }
}