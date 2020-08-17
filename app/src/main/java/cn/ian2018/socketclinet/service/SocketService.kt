package cn.ian2018.socketclinet.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import cn.ian2018.socketclinet.api.E2eeApi
import cn.ian2018.socketclinet.api.bean.Member
import cn.ian2018.socketclinet.db.RepositoryProvider
import cn.ian2018.socketclinet.db.data.MsgInfo
import cn.ian2018.socketclinet.db.repository.MsgInfoRepository
import cn.ian2018.socketclinet.event.ReceivedMsgEvent
import cn.ian2018.socketclinet.modle.*
import cn.ian2018.socketclinet.util.KeyUtil
import cn.ian2018.socketclinet.util.SPUtil
import com.dhh.websocket.RxWebSocket
import com.dhh.websocket.WebSocketSubscriber
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.WebSocket
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

/**
 * Created by chenshuai on 2020/8/7
 */
class SocketService : Service() {
    private var webSocket: WebSocket? = null

    private lateinit var msgInfoRepository: MsgInfoRepository

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        msgInfoRepository = RepositoryProvider.providerMsgInfoRepository(this)
        start()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val result = super.onStartCommand(intent, flags, startId)
        when (intent.getIntExtra("command", -1)) {
            1001 -> {
                val otherUserId = intent.getStringExtra("other")
                val connectRequest = ConnectRequest(otherUserId = otherUserId)
                sendData(Gson().toJson(connectRequest))
            }
            1002 -> {
                val msg = intent.getStringExtra("msg")
                val publicKey = intent.getStringExtra("key")
                val id = intent.getStringExtra("id")
                // 生成AES密钥
                val aesKey = KeyUtil.generateAESKey()
                // 使用对方的公钥加密AES密钥
                val encryptKey = KeyUtil.encryptData(publicKey, aesKey.toByteArray())
                // 使用AES加密消息
                val encryptMsg = KeyUtil.aesEncrypt(aesKey, (SPUtil.getId(this) + ": " + msg).toByteArray())
                // 构建聊天请求
                val chartRequest = ChartRequest(otherUserId = id, encryptKey = encryptKey, encryptMsg = encryptMsg, fromId = SPUtil.getId(this))
                sendData(Gson().toJson(chartRequest))
            }
            1003 -> {
                val groupId = intent.getIntExtra("groupId", 0)
                val connectGroupRequest = ConnectGroupRequest(userId = SPUtil.getId(this), groupId = groupId)
                sendData(Gson().toJson(connectGroupRequest))
            }
            1004 -> {
                val groupId = intent.getIntExtra("groupId", 0)
                val msg = intent.getStringExtra("msg")
                val members = intent.getParcelableArrayListExtra<Member>("members")
                // 生成AES密钥
                val aesKey = KeyUtil.generateAESKey()
                // 使用AES加密消息
                val encryptMsg = KeyUtil.aesEncrypt(aesKey, (SPUtil.getId(this) + ": " + msg).toByteArray())
                val memberKeys = mutableListOf<GroupMember>()
                members.forEach {
                    // 使用对方的公钥加密AES密钥
                    val encryptKey = KeyUtil.encryptData(it.publicKey, aesKey.toByteArray())
                    memberKeys.add(GroupMember(it.userId, encryptKey))
                }
                val charGroupRequest = ChartGroupRequest(fromId = SPUtil.getId(this), groupId = groupId, encryptMsg = encryptMsg, members = memberKeys)
                sendData(Gson().toJson(charGroupRequest))
            }
        }
        return result
    }

    private fun sendData(data: String) {
        //url 对应的WebSocket 必须打开,否则报错
        webSocket?.send(data)
    }

    private fun start() {
        webSocket?.close(0,"onReStart")
        webSocket?.cancel()
        RxWebSocket.get("ws://${E2eeApi.IP}:9503")
                .subscribe(object : WebSocketSubscriber() {
                    public override fun onOpen(webSocket: WebSocket) {
                        this@SocketService.webSocket = webSocket
                        Log.d("MainActivity", "onOpen:")
                        val initRequest = InitRequest(userId = SPUtil.getId(this@SocketService), publicKey = SPUtil.getPublicKey(this@SocketService))
                        sendData(Gson().toJson(initRequest))
                    }

                    public override fun onMessage(text: String) {
                        Log.d("MainActivity", "返回数据:$text")
                        CoroutineScope(Dispatchers.IO).launch {
                            processAction(text)
                        }
                    }

                    override fun onReconnect() {
                        Log.d("MainActivity", "重连:")
                    }

                    override fun onClose() {
                        Log.d("MainActivity", "onClose:")
                    }
                })
    }

    private fun processAction(text: String) {
        val jsonObject = JSONObject(text)
        when (jsonObject.getInt("action")) {
            S2CAction.ACTION_S2C_CHART_CONNECT -> {

            }
            S2CAction.ACTION_S2C_CHART -> {
                val msgInfo = parseMsg(jsonObject)

                // 插入到本地数据库
                msgInfoRepository.insertMsg(msgInfo)

                // 告知服务器 成功接受
                val successRequest = ReceiveSuccessRequest(msgId = msgInfo.serviceMsgId)
                sendData(Gson().toJson(successRequest))
            }
            S2CAction.ACTION_S2C_HISTORY_MSG -> {
                val msgs = jsonObject.getJSONArray("msgs")

                var msgIds = ""
                val msgList = mutableListOf<MsgInfo>()
                for (i in 0 until msgs.length()) {
                    val msg = msgs.getJSONObject(i)
                    val msgInfo = parseMsg(msg)
                    msgList.add(msgInfo)
                    msgIds += "${msgInfo.serviceMsgId},"
                }
                msgInfoRepository.insertMsgList(msgList)

                if (msgIds.isNotBlank()) {
                    val ids = msgIds.substring(0, msgIds.length - 1)
                    val historySuccessRequest = HistorySuccessRequest(msgIds = ids)
                    sendData(Gson().toJson(historySuccessRequest))
                }
            }
            S2CAction.ACTION_S2C_CONNECT_GROUP -> {

            }
        }
    }

    private fun parseMsg(jsonObject: JSONObject): MsgInfo {
        val aesKey = jsonObject.getString("aesKey")
        val msg = jsonObject.getString("msg")
        val time = jsonObject.getLong("time")
        val msgId = jsonObject.getInt("msgId")
        val ip = jsonObject.getString("ip")
        val groupId = jsonObject.getInt("groupId")
        val fromId = jsonObject.getString("fromId")

        // 解密AES key
        val key = String(KeyUtil.decryptData(SPUtil.getPrivateKey(this@SocketService), aesKey))
        // 解密消息
        val decryptMsg = String(KeyUtil.aesDecrypt(key, msg))
        val msgInfo = MsgInfo(decryptMsg, MsgInfo.TYPE_RECEIVED, ip, time, groupId, msgId, fromId)

        EventBus.getDefault().post(ReceivedMsgEvent(msgInfo))

        return msgInfo
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket?.close(0,"onDestroy")
        webSocket?.cancel()
    }

    companion object {

        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, SocketService::class.java)
            context.startService(intent)
        }

        fun connect(context: Context, otherId: String) {
            val intent = Intent(context, SocketService::class.java)
            intent.putExtra("command", 1001)
            intent.putExtra("other", otherId)
            context.startService(intent)
        }

        fun chart(context: Context, msg: String, id: String, key: String) {
            val intent = Intent(context, SocketService::class.java)
            intent.putExtra("command", 1002)
            intent.putExtra("msg", msg)
            intent.putExtra("id", id)
            intent.putExtra("key", key)
            context.startService(intent)
        }

        fun connectGroup(context: Context, groupId: Int) {
            val intent = Intent(context, SocketService::class.java)
            intent.putExtra("command", 1003)
            intent.putExtra("groupId", groupId)
            context.startService(intent)
        }

        fun chartGroup(context: Context, groupId: Int, msg: String, members: ArrayList<Member>) {
            val intent = Intent(context, SocketService::class.java)
            intent.putExtra("command", 1004)
            intent.putExtra("groupId", groupId)
            intent.putExtra("msg", msg)
            intent.putParcelableArrayListExtra("members", members)
            context.startService(intent)
        }
    }
}