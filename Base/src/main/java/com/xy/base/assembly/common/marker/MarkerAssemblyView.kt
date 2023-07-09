package com.xy.base.assembly.common.marker

import android.view.View
import com.xy.base.assembly.base.BaseAssemblyViewWithContext

interface MarkerAssemblyView : BaseAssemblyViewWithContext {
    fun scoringButtonView():View?
}