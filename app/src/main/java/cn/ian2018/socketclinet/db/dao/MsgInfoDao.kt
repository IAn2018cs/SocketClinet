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

    @Query("SELECT * FROM msgInfo")
    fun getAllMsg(): List<MsgInfo>

    @Delete
    fun deleteMsg(msg: MsgInfo)
}