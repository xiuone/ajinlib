package com.xy.qq.login

import android.view.View
import com.xy.base.assembly.load.BaseAssemblyViewLoadDialog

interface LoginAssemblyView : BaseAssemblyViewLoadDialog,LoginQQListener {
    fun onCreateQQLoginView(): View?
}