package cn.ian2018.socketclinet.api.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by chenshuai on 2020-05-09
 */
data class JoinGroupResult(
        val code: Int,
        val `data`: GroupData?,
        val message: String
)

data class QueryGroupResult(
        val code: Int,
        val `data`: List<GroupData>?,
        val message: String
)

@Parcelize
data class GroupData(
        val groupId: Int,
        val groupName: String,
        val members: ArrayList<Member>,
        val owner: String,
        val shareCode: String
) : Parcelable

@Parcelize
data class Member(
        val type: Int,
        val publicKey: String,
        val userId: String
) : Parcelable