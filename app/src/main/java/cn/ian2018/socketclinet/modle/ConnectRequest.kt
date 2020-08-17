package cn.ian2018.socketclinet.modle

/**
 * Created by chenshuai on 2020/8/13
 */
data class ConnectRequest(val action: Int = C2SAction.ACTION_C2S_CHART_CONNECT, val otherUserId: String)