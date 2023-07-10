package com.yalantis.ucrop.util

import android.content.Context
import android.content.res.Resources
import com.yalantis.ucrop.callback.BitmapLoadCallback
import com.yalantis.ucrop.task.BitmapLoadTask
import com.yalantis.ucrop.util.BitmapLoadUtils
import com.yalantis.ucrop.util.EglUtils
import kotlin.Throws
import androidx.annotation.RequiresApi
import com.yalantis.ucrop.util.RotationGestureDetector.OnRotationGestureListener
import com.yalantis.ucrop.util.RotationGestureDetector

/**
 * @author：luck
 * @date：2021/11/17 11:48 上午
 * @describe：DensityUtil
 */
object DensityUtil {
    /**
     * dp2px
     */
    @JvmStatic
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.applicationContext.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId =
            Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return if (result == 0) dip2px(context, 26f) else result
    }
}