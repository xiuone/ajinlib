package com.xy.base.utils

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Context
import android.content.ContextWrapper
import androidx.annotation.Keep

object ContextHolder {
    private val ANDROID_APP_ACTIVITY_THREAD = "android.app.ActivityThread"
    private val ANDROID_APP_APP_GLOBALS = "android.app.AppGlobals"
    private var sApplicationContext: Context? = null
    private var sCustomizeContext: Context? = null

    @Keep
    fun getContext(): Context? {
        if (sCustomizeContext != null) {
            return sCustomizeContext
        } else if (sApplicationContext == null) {
            try {
                val application = Class.forName(ANDROID_APP_ACTIVITY_THREAD)
                    .getMethod("currentApplication")
                    .invoke(null, null as Array<Any?>?) as Application
                if (application != null) {
                    sApplicationContext = application
                    return application
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                val ActivityThreadclz = Class.forName(ANDROID_APP_ACTIVITY_THREAD)
                val field = ActivityThreadclz.getDeclaredField("sCurrentActivityThread")
                field.isAccessible = true
                //得到ActivityThread的对象，虽然是隐藏的，但已经指向了内存的堆地址
                val currentActivity = field[null]
                val getApplicationMethod = ActivityThreadclz.getDeclaredMethod("getApplication")
                getApplicationMethod.isAccessible = true
                val application = getApplicationMethod.invoke(currentActivity) as Application
                if (application != null) {
                    sApplicationContext = application
                    return application
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                val application = Class.forName(ANDROID_APP_APP_GLOBALS)
                    .getMethod("getInitialApplication")
                    .invoke(null, null as Array<Any?>?) as Application
                if (application != null) {
                    sApplicationContext = application
                    return application
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            throw IllegalStateException(
                "ContextHolder is not initialed, it is recommend to init with application context.")
        }
        return sApplicationContext
    }

    @Keep
    fun setContext(context: Context?) {
        if (context != null) {
            if (context is Application) {
                sCustomizeContext = context
                return
            } else if (context is ContextWrapper &&
                !(context is Activity || context is Service)
            ) {
                if (context.baseContext is Application) {
                    sCustomizeContext = context
                    return
                }
            }
        }
        throw IllegalStateException(
            "The context must be ApplicationContext or a ContextWrapper based on ApplicationContext")
    }
}