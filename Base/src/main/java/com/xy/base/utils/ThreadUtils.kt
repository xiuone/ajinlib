package com.xy.base.utils

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper

private val mainHandler by lazy { Handler(Looper.getMainLooper()) }
private val backHandler by lazy { createAppBackHandler("background") }

private fun createAppBackHandler(name: String): Handler {
    val backThread = HandlerThread(name)
    return Handler(backThread.looper)
}

fun <T> T.getBackProgress() = backHandler

fun <T> T.getMainProgress() = mainHandler

fun <T> T.createBackHandler(name: String): Handler  = createAppBackHandler(name)


fun <T> T.runBackThread(runnable: Runnable,delayMillis:Long = 0) = backHandler.runProgress(runnable, delayMillis)

fun <T> T.runMain(runnable: Runnable,delayMillis:Long = 0) = mainHandler.runProgress(runnable, delayMillis)

fun Handler.runProgress(runnable: Runnable,delayMillis:Long = 0){
    if (Thread.currentThread() == looper.thread && delayMillis <= 0){
        runnable.run()
    }else {
        if (delayMillis <= 0){
            post(runnable)
        }else{
            postDelayed(runnable,delayMillis)
        }
    }
}