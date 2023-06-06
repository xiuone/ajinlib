package com.xy.libad.video

import com.xy.base.assembly.base.BaseAssemblyView

interface AdVideoAssemblyView : BaseAssemblyView {
    fun adVideoOver()
    fun adVideoError()
    fun adRewardVerify(){}
}