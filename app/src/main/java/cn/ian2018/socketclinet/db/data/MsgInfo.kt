package cn.ian2018.socketclinet.db.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by chenshuai on 2020-05-07
 */
@Entity(tableName = "msgInfo")
data class MsgInfo(
        @ColumnInfo(name = "msg") val msg: String,
        @ColumnInfo(name = "type") val type: Int,
        @ColumnInfo(name = "ip") val ip: String,
        @ColumnInfo(name = "time") val time: Long,
        @ColumnInfo(name = "groupId", defaultValue = "-1") val groupId: Int = -1,
        @ColumnInfo(name = "serviceMsgId") val serviceMsgId: Int = -1,
        @ColumnInfo(name = "fromId") val fromId: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0

    fun getFormatTime(): String {
        return SimpleDateFormat("MMM.d  HH:mm", Locale.getDefault()).format(time)
    }

    companion object {
        const val TYPE_RECEIVED = 0
        const val TYPE_SEND = 1
    }
}