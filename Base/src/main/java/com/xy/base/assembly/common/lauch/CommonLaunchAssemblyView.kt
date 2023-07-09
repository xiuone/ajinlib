package com.xy.base.assembly.common.lauch

import android.view.View
import com.xy.base.assembly.base.BaseAssemblyViewWithContext

interface CommonLaunchAssemblyView : BaseAssemblyViewWithContext {
    /**
     * 同意按钮
     */
    fun agreeButtonView():View?
    /**
     * 拒绝按钮
     */
    fun refuseButtonView():View?

    /**
     * 隐私政策所有的显示或者隐藏
     */
    fun privacyContentView():View?

    fun agreeLaunchPrivacy()
}