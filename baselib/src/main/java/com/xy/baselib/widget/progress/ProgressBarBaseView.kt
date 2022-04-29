package com.xy.baselib.widget.progress

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.xy.baselib.R
import com.xy.utils.addAlpha
import com.xy.utils.getResDimension
import kotlin.math.max
import kotlin.math.min

abstract class ProgressBarBaseView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    :ProgressStripView(context, attrs, defStyleAttr)  {

    private var thumbShadowColor : Int = white
    private var thumbColor : Int = white
    protected var thumbShadowRadius : Float = 0F
    //监听滑动进度
    var progressListener:ProgressListener ? = null;
    protected val animHelper:ProgressAnimHelper by lazy { ProgressAnimHelper(this) }

    init {
        attrs?.run {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressView)
            thumbColor = typedArray.getColor(R.styleable.ProgressView_progress_thumb_color, white)
            thumbShadowColor = typedArray.getColor(R.styleable.ProgressView_progress_thumb_shadow_color, white)
            thumbShadowRadius = typedArray.getDimension(R.styleable.ProgressView_progress_thumb_shadow_radius, context.getResDimension(R.dimen.dp_2).toFloat())
        }
    }

    override fun startRight(): Float  = width - startLeft()
    override fun startLength(): Float = startLeft()
    override fun defaultShowTv(): Boolean = false

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null)return true;
        onActionTouchProgress(event.x)
        invalidate()
        progressListener?.onProgressCallBack(progress)
        animHelper.startAnim(event)
        return true
    }

    /**
     * 计算当前移动的进度
     */
    private fun onActionTouchProgress(touchX:Float){
        progress = when {
            touchX < startLeft() -> 0
            touchX > startRight() -> 100
            else -> {
                val width = startRight() - startLeft();
                val currentX = touchX - startLeft();
                (currentX*100/width).toInt();
            }
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        val width = startRight() - startLeft()
        val thumbX = startLeft() + progress * width / 100
        val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        thumbPaint.color = thumbColor
        thumbPaint.setShadowLayer(thumbShadowRadius, 0F, 0F, thumbShadowColor.addAlpha("77"))
        drawThumb(canvas,thumbX,thumbPaint)
    }

    /**
     * 绘制操作杆
     */
    abstract fun drawThumb(canvas: Canvas,thumbX:Float,thumbPaint :Paint)

    override fun onDestroyed(owner: LifecycleOwner) {
        super.onDestroyed(owner)
        animHelper.onDestroyed()
    }
}