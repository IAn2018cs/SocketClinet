package cn.ian2018.socketclinet.db

import android.content.Context
import cn.ian2018.socketclinet.db.repository.MsgInfoRepository

/**
 * Created by chenshuai on 2020-05-07
 */
object RepositoryProvider {

    fun providerMsgInfoRepository(context: Context) =
            MsgInfoRepository.getInstance(AppDatabase.getInstance(context).msgInfoDao())

}