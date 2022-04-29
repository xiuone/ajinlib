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
import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.xy.baselib.R
import com.xy.utils.Logger
import com.xy.utils.addAlpha
import com.xy.utils.getResDimension
import kotlin.math.max
import kotlin.math.min

class ProgressBar2View @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    :ProgressBarBaseView(context, attrs, defStyleAttr)  {
    private var thumbMinSize :Int =  -1
            get() {
                return if (field <=0) height else if (field>=height) height else field
            }
    private var thumbMaxSize : Int = -1
            get() {
                return if (field <=0) height else if (field>=height) height else field
            }
    //进度条高度
    private var progressHeight : Int = -1
            get() {
                return if (field <= 0) thumbMinSize/2 else field
            }


    init {
        attrs?.run {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressView)
            val thumbMinSize = typedArray.getDimensionPixelOffset(R.styleable.ProgressView_progress_thumb_min_size, -1)
            val thumbMaxSize = typedArray.getDimensionPixelOffset(R.styleable.ProgressView_progress_thumb_max_size, -1)
            progressHeight = typedArray.getDimensionPixelOffset(R.styleable.ProgressView_progress_height, -1)
            val thumbMax = max(thumbMaxSize,thumbMinSize)
            val thumbMin = min(thumbMaxSize,thumbMinSize)
            this@ProgressBar2View.thumbMaxSize = thumbMax
            this@ProgressBar2View.thumbMinSize = thumbMin
        }
    }

    override fun startLeft(): Float  = thumbMaxSize/2F+thumbShadowRadius*2
    override fun startTop(): Float  = (height-progressHeight)/2F
    override fun startBottom(): Float = startTop() + progressHeight;

    override fun drawThumb(canvas: Canvas, thumbX: Float, thumbPaint: Paint) {
        var radius = (thumbMinSize + (thumbMaxSize - thumbMinSize)*animHelper.animValue)
        val realRadius = radius+thumbShadowRadius*4
        if (realRadius > height){
            radius = height - thumbShadowRadius*4
        }
        val thumbY = (startBottom() + startTop())/2
        canvas.drawCircle(thumbX,thumbY,radius/2,thumbPaint)
    }
}