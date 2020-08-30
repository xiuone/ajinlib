package com.jianbian.baselib

import android.content.Context
import androidx.multidex.MultiDexApplication

abstract class BaseApp :MultiDexApplication() {
    companion object{
        var context:Context ?= null
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}