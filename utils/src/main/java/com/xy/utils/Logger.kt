package com.xy.utils

import android.util.Log

/**
 * Created by wangyalei on 18/1/5.
 */
object Logger {
    const val TAG = "==``"

    /**
     * 类名
     */
    private var sClassName: String? = null

    /**
     * 方法名
     */
    private var sMethodName: String? = null

    /**
     * 行数
     */
    private var sLineNumber = 0

    fun d(msg: String) {
        d(TAG, msg)
    }

    fun d(tag: String?, msg: String) {
        if (BuildConfig.DEBUG) {
            getMethodNames(Throwable().stackTrace)
            Log.d(tag, createLog(msg))
        }
    }

    fun d(tag: String?, msg: String, e: Exception?) {
        if (BuildConfig.DEBUG) {
            getMethodNames(Throwable().stackTrace)
            Log.d(tag, createLog(msg), e)
        }
    }

    fun e(msg: String?) {
        e(TAG, msg)
    }

    fun e(msg: Throwable?) {
        e(TAG, msg?.message);
    }

    fun e(tag: String?, msg: String?) {
        if (BuildConfig.DEBUG && !msg.isNullOrEmpty()) {
            getMethodNames(Throwable().stackTrace)
            Log.e(tag, createLog(msg))
        }
    }

    private fun createLog(log: String): String {
        return "[" +
                sMethodName +
                ":" +
                sLineNumber +
                "]" +
                log
    }

    private fun getMethodNames(sElements: Array<StackTraceElement>) {
        sClassName = sElements[1].fileName
        sMethodName = sElements[1].methodName
        sLineNumber = sElements[1].lineNumber
    }
}