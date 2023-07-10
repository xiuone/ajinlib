package xy.xy.base.widget.swipe.mode

import androidx.interpolator.view.animation.LinearOutSlowInInterpolator

object SwipeBackConfig {
    var INTERPOLATOR = LinearOutSlowInInterpolator()
    const val MIN_ALPHA = 55
    const val ANIMATOR_DURATION = 100L
    const val PERCENT_MAX_ALPHA = 200

    const val START_ANGLE = 50f
    const val PERCENT_MAX_ANGLE = 65f

    const val GOLDEN_RATIO = 0.382f
    const val GOLDEN_RATIO_LARGE = 0.618f
    const val REAL_PEAK_RATIO = 2.7272727f
    const val SHAPE_MAX_PEAK = 100f
}