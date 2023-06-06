package com.xy.libad.lanuch

import android.view.ViewGroup
import com.xy.base.assembly.base.BaseAssemblyView

interface AdLauncherAssemblyView : BaseAssemblyView {
    fun adLauncherOver()
    fun onLauncherViewGroup():ViewGroup?
}