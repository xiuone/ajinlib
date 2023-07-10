package com.xy.wechat.login

import android.view.View
import xy.xy.base.assembly.load.BaseAssemblyViewLoadDialog

interface WxLoginAssemblyView : BaseAssemblyViewLoadDialog,LoginWxListener {
    fun onCreateWxLoginView(): View?
}