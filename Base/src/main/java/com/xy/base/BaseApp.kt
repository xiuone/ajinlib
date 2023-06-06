package com.xy.base

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.xy.base.utils.ContextHolder
import com.xy.base.utils.Logger
import com.xy.base.utils.lift.ActivityController
import com.xy.base.utils.config.font.ConfigChangeListener
import com.xy.base.utils.config.ConfigController
import com.xy.base.web.down.DownloadManager
import me.jessyan.autosize.AutoSize


abstract class BaseApp : MultiDexApplication() , ConfigChangeListener {
    private val configController by lazy { ConfigController() }

    override fun onCreate() {
        super.onCreate()
        AutoSize.initCompatMultiProcess(this)
        registerActivityLifecycleCallbacks(ActivityController.instance)
        ContextHolder.setContext(this)
        Logger.debug = BuildConfig.DEBUG
        DownloadManager.instance.init()
    }

    override fun attachBaseContext(newBase: Context) {
        val config = configController.changeConfig(newBase)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onChangeConfig() {
        configController.checkChangeConfig(this)
    }

}