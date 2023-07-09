package com.xy.libad.lanuch

import android.view.ViewGroup
import com.xy.base.assembly.base.BaseAssemblyViewWithContext

interface AdLauncherAssemblyView : BaseAssemblyViewWithContext {
    fun adLauncherOver()
    fun onLauncherViewGroup():ViewGroup?
}