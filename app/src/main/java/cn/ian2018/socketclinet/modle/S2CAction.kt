package cn.ian2018.socketclinet.modle

/**
 * Created by chenshuai on 2020/8/14
 */
object S2CAction {
    const val ACTION_S2C_CHART_CONNECT = 1 // 返回被连接者公钥
    const val ACTION_S2C_CHART = 2 // 聊天通信
    const val ACTION_S2C_HISTORY_MSG = 3 // 返回历史消息
    const val ACTION_S2C_CONNECT_GROUP = 4 // 返回群内成员公钥
}