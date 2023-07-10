package com.xy.qq.login

import android.view.View
import xy.xy.base.assembly.load.BaseAssemblyViewLoadDialog

interface LoginAssemblyView : BaseAssemblyViewLoadDialog,LoginQQListener {
    fun onCreateQQLoginView(): View?
}