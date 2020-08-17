package cn.ian2018.socketclinet.util

import android.util.Log
import cn.ian2018.socketclinet.BuildConfig

/**
 * Created by chenshuai on 2020-05-09
 */
object Logger {

    private const val TAG = "e2eeTest"

    private val DEBUG = BuildConfig.DEBUG

    fun d(tag: String = TAG, msg: String) {
        if (DEBUG) {
            Log.d(tag, msg)
        }
    }

    fun e(tag: String = TAG, msg: String) {
        if (DEBUG) {
            Log.e(tag, msg)
        }
    }

    fun i(tag: String = TAG, msg: String) {
        if (DEBUG) {
            Log.i(tag, msg)
        }
    }

    fun w(tag: String = TAG, msg: String) {
        if (DEBUG) {
            Log.w(tag, msg)
        }
    }
}