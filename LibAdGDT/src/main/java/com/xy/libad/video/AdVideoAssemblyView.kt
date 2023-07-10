package com.xy.libad.video

import xy.xy.base.assembly.base.BaseAssemblyViewWithContext

interface AdVideoAssemblyView : BaseAssemblyViewWithContext {
    fun adVideoOver()
    fun adVideoError()
    fun adRewardVerify(){}
}