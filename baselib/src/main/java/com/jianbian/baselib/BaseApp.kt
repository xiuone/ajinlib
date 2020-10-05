package com.jianbian.baselib

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout


abstract class BaseApp :MultiDexApplication() {
    companion object{
        var context:Context ?= null
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.gray_f2f2, R.color.gray_9999) //全局设置主题颜色
            ClassicsHeader(context)
        }

        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ -> //指定为经典Footer，默认是 BallPulseFooter
            ClassicsFooter(context).setDrawableSize(20F)
        }
    }
}