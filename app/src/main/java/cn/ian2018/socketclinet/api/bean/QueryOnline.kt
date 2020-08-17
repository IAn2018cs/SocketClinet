package cn.ian2018.socketclinet.api.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by chenshuai on 2020/8/14
 */
data class QueryOnlineResult(
        val code: Int,
        val `data`: List<UserData>?,
        val message: String
)

@Parcelize
data class UserData(
        val publicKey: String,
        val userId: String
) : Parcelable