package cn.ian2018.socketclinet.modle

/**
 * Created by chenshuai on 2020/8/15
 */
data class ConnectGroupRequest(val action: Int = C2SAction.ACTION_C2S_CONNECT_GROUP, val userId: String, val groupId: Int)