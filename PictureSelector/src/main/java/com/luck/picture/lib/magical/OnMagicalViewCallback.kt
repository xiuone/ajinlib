package com.luck.picture.lib.magical

import com.luck.picture.lib.config.SelectorProviders.Companion.instance
import com.luck.picture.lib.config.SelectorProviders.selectorConfig
import com.luck.picture.lib.basic.InterpolatorFactory.newInterpolator
import kotlin.jvm.JvmOverloads
import com.luck.picture.lib.config.SelectorConfig
import androidx.annotation.RequiresApi
import androidx.viewpager2.widget.ViewPager2
import com.luck.picture.lib.config.SelectorProviders

/**
 * @author：luck
 * @date：2021/12/15 11:06 上午
 * @describe：OnMagicalViewCallback
 */
interface OnMagicalViewCallback {
    fun onBeginBackMinAnim()
    fun onBeginBackMinMagicalFinish(isResetSize: Boolean)
    fun onBeginMagicalAnimComplete(mojitoView: MagicalView?, showImmediately: Boolean)
    fun onBackgroundAlpha(alpha: Float)
    fun onMagicalViewFinish()
}