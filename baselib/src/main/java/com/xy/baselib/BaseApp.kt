package com.xy.baselib

import android.content.Context
import androidx.annotation.NonNull
import androidx.multidex.MultiDexApplication
import com.luck.picture.lib.engine.ImageEngine
import com.xy.baselib.utils.ActivityController
import me.jessyan.autosize.AutoSize


abstract class BaseApp :MultiDexApplication() {
    companion object{
        var context:Context ?= null
        var engine: ImageEngine?=null
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        AutoSize.initCompatMultiProcess(this)
        registerActivityLifecycleCallbacks(ActivityController.controller)
        engine = getEngine()
    }

    abstract fun getEngine():ImageEngine?

}