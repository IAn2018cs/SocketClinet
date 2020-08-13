package cn.ian2018.socketclinet.modle

/**
 * Created by chenshuai on 2020/8/13
 */
data class InitRequest(val action: String = "init", val userId: String, val publicKey: String)