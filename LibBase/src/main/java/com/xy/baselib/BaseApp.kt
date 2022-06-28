package com.xy.baselib

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator
import com.xy.baselib.config.BaseObject
import com.xy.baselib.exp.Logger
import com.xy.baselib.exp.getResColor
import com.xy.baselib.net.init
import com.xy.baselib.widget.refresh.header.WaterDropHeader
import me.jessyan.autosize.AutoSize


abstract class BaseApp : MultiDexApplication() {

    fun setDebug(boolean: Boolean){
        Logger.debug = boolean
        init(boolean)
    }

    override fun onCreate() {
        super.onCreate()
        AutoSize.initCompatMultiProcess(this)
        registerActivityLifecycleCallbacks(BaseObject.actController)
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(DefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.white, android.R.color.white) //全局设置主题颜色
            return@DefaultRefreshHeaderCreator WaterDropHeader(context)
        })
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ -> //指定为经典Footer，默认是 BallPulseFooter
            val pulseFooter = BallPulseFooter(context);
            val color1 = context.getResColor(R.color.gray_fafa)
            val color2 = context.getResColor(R.color.gray_e5e5)
            val color3 = context.getResColor(R.color.gray_9999)
            pulseFooter.setPrimaryColors(color1,color2,color3)
            return@setDefaultRefreshFooterCreator pulseFooter
        }
    }


    override fun attachBaseContext(newBase: Context) {
        val res = newBase.resources
        val config = res.configuration
        config.fontScale = BaseObject.fontManger.getFontScaleSize(newBase)
        config.setLocale(BaseObject.languageManger.getCurrentLanguage(newBase).locales)
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }


    fun updateConfiguration() {
        val languageMode = BaseObject.languageManger.getCurrentLanguage(this)
        val fontScaleSize = BaseObject.fontManger.getFontScaleSize(this)
        val config = resources.configuration
        config.setLocale(languageMode.locales)
        config.fontScale = fontScaleSize
        resources.updateConfiguration(config, null)
        resources.flushLayoutCache()
        resources.updateConfiguration(config, resources.displayMetrics)
    }

}