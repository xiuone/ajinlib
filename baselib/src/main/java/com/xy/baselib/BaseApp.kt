package com.xy.baselib

import android.content.Context
import androidx.annotation.NonNull
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import androidx.multidex.MultiDexApplication
import me.jessyan.autosize.AutoSize


abstract class BaseApp :MultiDexApplication(), CameraXConfig.Provider {
    companion object{
        var context:Context ?= null
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        AutoSize.initCompatMultiProcess(this)
    }

    @NonNull
    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }
}