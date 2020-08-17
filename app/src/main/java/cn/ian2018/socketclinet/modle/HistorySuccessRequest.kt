package cn.ian2018.socketclinet.modle

/**
 * Created by chenshuai on 2020/8/13
 */
data class HistorySuccessRequest(val action: Int = C2SAction.ACTION_C2S_HISTORY_RECEIVED, val msgIds: String)