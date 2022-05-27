package com.xy.baselib

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.xy.baselib.config.BaseConfig
import me.jessyan.autosize.AutoSize


abstract class BaseApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        AutoSize.initCompatMultiProcess(this)
        registerActivityLifecycleCallbacks(BaseConfig.actController)
    }

    override fun attachBaseContext(newBase: Context) {
        val res = newBase.resources
        val config = res.configuration
        config.fontScale = BaseConfig.fontManger.getFontScaleSize(newBase)
        config.setLocale(BaseConfig.languageManger.getCurrentLanguage(newBase).locales)
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }


    fun updateConfiguration() {
        val languageMode = BaseConfig.languageManger.getCurrentLanguage(this)
        val fontScaleSize = BaseConfig.fontManger.getFontScaleSize(this)
        val config = resources.configuration
        config.setLocale(languageMode.locales)
        config.fontScale = fontScaleSize
        resources.updateConfiguration(config, null)
        resources.flushLayoutCache()
        resources.updateConfiguration(config, resources.displayMetrics)
    }

}