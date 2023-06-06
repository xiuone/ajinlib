package com.xy.wechat.login

import android.view.View
import com.xy.base.assembly.load.BaseAssemblyViewLoadDialog

interface WxLoginAssemblyView : BaseAssemblyViewLoadDialog,LoginWxListener {
    fun onCreateWxLoginView(): View?
}