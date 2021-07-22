package com.xy.baselib.ui.act

import android.view.View

interface BaseActListener {
    fun registerEventBus():Boolean
    fun statusBarView(): View?
    fun statusBarDurk():Boolean
    fun setContentLayout(view: View?):Boolean
    fun setTitleView(view: View?):Boolean
    fun setErrorView(view: View?):Boolean
    fun setPreloadingView(view: View?):Boolean
    fun keyboardMode():Int
    fun keyboardEnable():Boolean

}