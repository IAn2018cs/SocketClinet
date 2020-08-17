package cn.ian2018.socketclinet.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import cn.ian2018.socketclinet.db.data.MsgInfo

/**
 * Created by chenshuai on 2020-05-07
 */
@Dao
interface MsgInfoDao {

    @Insert
    fun insertMsg(msg: MsgInfo)

    @Insert
    fun insertMsgList(msg: List<MsgInfo>)

    @Query("SELECT * FROM msgInfo WHERE groupId = -1 AND fromId = :fromId")
    fun getAllMsg(fromId: String): List<MsgInfo>

    @Query("SELECT * FROM msgInfo WHERE groupId = :groupId")
    fun getGroupMsg(groupId: Int): List<MsgInfo>

    @Delete
    fun deleteMsg(msg: MsgInfo)
}