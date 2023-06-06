package com.xy.base.widget.photoview

import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator

class InterpolatorProxy : Interpolator {
    var mTarget: Interpolator = DecelerateInterpolator()

    override fun getInterpolation(input: Float): Float {
        return mTarget?.getInterpolation(input)?: input
    }
}