package com.xy.libad.video

import com.xy.base.assembly.base.BaseAssemblyViewWithContext

interface AdVideoAssemblyView : BaseAssemblyViewWithContext {
    fun adVideoOver()
    fun adVideoError()
    fun adRewardVerify(){}
}