package cn.ian2018.socketclinet.modle

/**
 * Created by chenshuai on 2020/8/13
 */
data class ConnectRequest(val action: String = "connect", val otherUserId: String)