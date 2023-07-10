package xy.xy.base.utils

import android.util.Log

object Logger {
    const val TAG = "==``"

    /**
     * 类名
     */
    private var sClassName: String? = null
    var debug = true

    /**
     * 方法名
     */
    private var sMethodName: String? = null

    /**
     * 行数
     */
    private var sLineNumber = 0

    fun i(tag: String?, msg: String) {
        if (debug) {
            getMethodNames(Throwable().stackTrace)
            Log.i(tag, createLog(msg))
        }
    }

    fun v(tag: String?, msg: String) {
        if (debug) {
            getMethodNames(Throwable().stackTrace)
            Log.v(tag, createLog(msg))
        }
    }

    fun w(tag: String?, msg: String) {
        if (debug) {
            getMethodNames(Throwable().stackTrace)
            Log.w(tag, createLog(msg))
        }
    }

    fun w(tag: String?, msg: String, e: Exception?) {
        if (debug) {
            getMethodNames(Throwable().stackTrace)
            Log.w(tag, createLog(msg), e)
        }
    }

    fun d(msg: String) {
        d(TAG, msg)
    }

    fun d(tag: String?, msg: String) {
        if (debug) {
            getMethodNames(Throwable().stackTrace)
            Log.d(tag, createLog(msg))
        }
    }

    fun d(tag: String?, msg: String, e: Exception?) {
        if (debug) {
            getMethodNames(Throwable().stackTrace)
            Log.d(tag, createLog(msg), e)
        }
    }

    fun e(msg: String?) {
        e(TAG, msg)
    }

    fun e(msg: Throwable?) {
        e(TAG, msg?.message)
    }

    fun e(tag: String?, msg: String?) {
        if (debug && !msg.isNullOrEmpty()) {
            getMethodNames(Throwable().stackTrace)
            Log.e(tag, createLog(msg))
        }
    }


    fun e(tag: String?, msg: String, e: Exception?) {
        if (debug) {
            getMethodNames(Throwable().stackTrace)
            Log.e(tag, createLog(msg), e)
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