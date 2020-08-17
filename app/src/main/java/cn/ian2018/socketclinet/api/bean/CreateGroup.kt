package cn.ian2018.socketclinet.api.bean


/**
 * Created by chenshuai on 2020/7/21
 */
data class CreateGroupResult(
        val code: Int,
        val `data`: CreateGroupData?,
        val message: String
)

data class CreateGroupData(
        val groupId: Int,
        val shareCode: String
)
