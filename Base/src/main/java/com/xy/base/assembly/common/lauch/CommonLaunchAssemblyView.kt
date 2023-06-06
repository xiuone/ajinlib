package com.xy.base.assembly.common.lauch

import android.view.View
import android.widget.TextView
import com.xy.base.assembly.base.BaseAssemblyView

interface CommonLaunchAssemblyView : BaseAssemblyView {
    /**
     * 同意按钮
     */
    fun agreeButtonView():View?
    /**
     * 拒绝按钮
     */
    fun refuseButtonView():View?

    /**
     * 政策的view 提示信心的view
     */
    fun agreementTextView(): TextView?

    /**
     * 隐私政策所有的显示或者隐藏
     */
    fun privacyContentView():View?

    fun agreeLaunchPrivacy()
}