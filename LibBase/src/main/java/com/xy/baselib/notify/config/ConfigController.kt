package com.xy.baselib.notify.config

import android.content.Context
import android.content.res.Configuration
import com.xy.baselib.config.BaseObject
import com.xy.baselib.notify.config.ConfigListener

class ConfigController  {

    fun attachBaseContext(newBase: Context): Context {
        val config = changeConfig(newBase)
        return newBase.createConfigurationContext(config)
    }

    private fun changeConfig(newBase: Context): Configuration {
        val res = newBase.resources
        val config = res.configuration
        config.setLocale(BaseObject.languageManger.getCurrentLanguage(newBase).locales)
        config.fontScale = BaseObject.fontManger.getFontScaleSize(newBase)
        return config
    }

    fun onChangeConfig(context: Context) {
        val res = context.resources
        val config = changeConfig(context)
        res.updateConfiguration(config, null)
        res.flushLayoutCache()
        res.updateConfiguration(config, res.displayMetrics)
    }

}