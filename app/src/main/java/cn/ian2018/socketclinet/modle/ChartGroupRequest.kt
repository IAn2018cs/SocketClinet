package cn.ian2018.socketclinet.modle

/**
 * Created by chenshuai on 2020/8/15
 */
data class ChartGroupRequest(val action: Int = C2SAction.ACTION_C2S_CHART_GROUP, val fromId: String, val groupId: Int, val encryptMsg: String, val members: List<GroupMember>)

data class GroupMember(val userId: String, val aesKey: String)