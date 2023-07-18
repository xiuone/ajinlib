package camerax.luck.lib.camerax.widget

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
    private var mFocusImg = 0
    private var mFocusSucceedImg = 0
    private var mFocusFailedImg = 0
    private val mAnimation by lazy { AnimationUtils.loadAnimation(context, R.anim.focusview_show) }
    private val mHandler by lazy { Handler(Looper.getMainLooper()) }

    private var isDisappear = false

    init {
        visibility = GONE
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FocusImageView)
        mFocusImg = typedArray.getResourceId(R.styleable.FocusImageView_focus_focusing, R.drawable.focus_focusing)
        mFocusSucceedImg = typedArray.getResourceId(R.styleable.FocusImageView_focus_success, R.drawable.focus_focused)
        mFocusFailedImg = typedArray.getResourceId(R.styleable.FocusImageView_focus_error, R.drawable.focus_failed)
        typedArray.recycle()
    }

    fun setDisappear(disappear: Boolean) {
        isDisappear = disappear
    }

    fun startFocus(point: Point) {
        val params = layoutParams as RelativeLayout.LayoutParams
        params.topMargin = point.y - measuredHeight / 2
        params.leftMargin = point.x - measuredWidth / 2
        layoutParams = params
        visibility = VISIBLE
        setFocusResource(mFocusImg)
        startAnimation(mAnimation)
    }

    fun onFocusSuccess() {
        if (isDisappear) {
            setFocusResource(mFocusSucceedImg)
        }
        mHandler.removeCallbacksAndMessages(null)
        mHandler.postDelayed({ setFocusGone() }, DELAY_MILLIS)
    }

    fun onFocusFailed() {
        if (isDisappear) {
            setFocusResource(mFocusFailedImg)
        }
        mHandler.removeCallbacksAndMessages(null)
        mHandler.postDelayed({ setFocusGone() }, DELAY_MILLIS)
    }

    private fun setFocusResource(@DrawableRes resId: Int) {
        setImageResource(resId)
    }

    private fun setFocusGone() {
        if (isDisappear) {
            visibility = GONE
        }
    }

    fun destroy() {
        mHandler.removeCallbacksAndMessages(null)
        visibility = GONE
    }
}