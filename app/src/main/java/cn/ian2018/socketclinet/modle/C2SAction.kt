package cn.ian2018.socketclinet.modle

/**
 * Created by chenshuai on 2020/8/14
 */
object C2SAction {
    const val ACTION_C2S_ONLINE = 1 // 用户连接上线
    const val ACTION_C2S_CHART_CONNECT = 2 // 请求连接
    const val ACTION_C2S_CHART = 3 // 聊天通信
    const val ACTION_C2S_RECEIVED = 4 // 成功接收信息
    const val ACTION_C2S_HISTORY_RECEIVED = 5 // 成功接收史信息
    const val ACTION_C2S_CONNECT_GROUP = 6 // 请求连接组
    const val ACTION_C2S_CHART_GROUP = 7 // 组聊天通信
}