package cn.ian2018.socketclinet.db.repository

import cn.ian2018.socketclinet.db.dao.MsgInfoDao
import cn.ian2018.socketclinet.db.data.MsgInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by chenshuai on 2020-05-07
 */
class MsgInfoRepository private constructor(private val msgInfoDao: MsgInfoDao) {

    fun insertMsg(msg: MsgInfo) {
        CoroutineScope(Dispatchers.IO).launch {
            msgInfoDao.insertMsg(msg)
        }
    }

    suspend fun getAllMsg() = withContext(Dispatchers.IO) {
        return@withContext msgInfoDao.getAllMsg()
    }

    fun deleteMsg(msg: MsgInfo) {
        CoroutineScope(Dispatchers.IO).launch {
            msgInfoDao.deleteMsg(msg)
        }
    }

    companion object {
        @Volatile
        private var instance: MsgInfoRepository? = null

        fun getInstance(msgInfoDao: MsgInfoDao): MsgInfoRepository =
                instance ?: synchronized(this) {
                    instance
                            ?: MsgInfoRepository(msgInfoDao).also {
                                instance = it
                            }
                }
    }
}