package cn.ian2018.socketclinet.modle

/**
 * Created by chenshuai on 2020/8/13
 */
data class ChartRequest(val action: String = "chart", val otherUserId: String, val encryptKey: String, val encryptMsg: String)