package camerax.luck.lib.camerax.widget.focus

import android.content.Context
import android.graphics.Point
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import xy.xy.base.R

/**
 * @author：luck
 * @date：2022-02-12 13:41
 * @describe：FocusImageView
 */
class FocusImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AppCompatImageView(context, attrs, defStyleAttr) {
    private val DELAY_MILLIS by lazy { 1000L }
    private val mAnimation by lazy { AnimationUtils.loadAnimation(context, R.anim.focusview_show) }
    private val mHandler by lazy { Handler(Looper.getMainLooper()) }
    var isDisappear = false

    init {
        visibility = GONE
    }

    fun startFocus(point: Point) {
        val params = layoutParams as RelativeLayout.LayoutParams
        params.topMargin = point.y - measuredHeight / 2
        params.leftMargin = point.x - measuredWidth / 2
        layoutParams = params
        visibility = VISIBLE
        setImageResource(R.drawable.focus_focusing)
        startAnimation(mAnimation)
    }

    fun onFocusSuccess() {
        if (isDisappear) {
            setImageResource(R.drawable.focus_focused)
        }
        mHandler.removeCallbacksAndMessages(null)
        mHandler.postDelayed({ setFocusGone() }, DELAY_MILLIS)
    }

    fun onFocusFailed() {
        if (isDisappear) {
            setImageResource(R.drawable.focus_failed)
        }
        mHandler.removeCallbacksAndMessages(null)
        mHandler.postDelayed({ setFocusGone() }, DELAY_MILLIS)
    }

    private fun setFocusGone() {
        if (isDisappear) {
            visibility = GONE
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mHandler.removeCallbacksAndMessages(null)
        visibility = GONE
    }
}