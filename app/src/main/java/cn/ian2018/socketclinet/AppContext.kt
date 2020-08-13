package cn.ian2018.socketclinet

import android.app.Application
import android.content.Context
import com.amber.lib.config.GlobalConfig
import com.dhh.websocket.Config
import com.dhh.websocket.RxWebSocket
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Created by chenshuai on 2020/8/6
 */
class AppContext : Application() {


    override fun onCreate() {
        super.onCreate()
        context = this
        GlobalConfig.getInstance().init(this)

        //init config 在使用RxWebSocket之前设置即可，推荐在application里初始化
        val config = Config.Builder()
                .setShowLog(true) //show  log
                .setClient(OkHttpClient.Builder()
                        .pingInterval(3, TimeUnit.SECONDS) // 设置心跳间隔，这个是3秒检测一次
                        .build())
                .setShowLog(true, "CHEN")
                .setReconnectInterval(2, TimeUnit.SECONDS) //set reconnect interval
                .build()
        RxWebSocket.setConfig(config)
    }

    companion object {
        lateinit var context: Context
    }
}