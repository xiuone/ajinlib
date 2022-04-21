package com.xy.baselib.widget.progress

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.xy.baselib.R
import com.xy.utils.drawCenterText

abstract class ProgressBaseView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) ,Runnable, LifecycleObserver {
    protected var progress :Int = 0
    protected var mBackgroundColor = -0x1a1a16
    protected var progressColor = -0xc9ac01
    protected var progressTvColor = -0x1
    protected var progressTvSize = 48





    fun updateProgress(progress: Int) {
        var progress = progress
        if (progress < 0) progress = 0
        if (progress > 100) progress = 100
        this.progress = progress
        invalidate()
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed(this, 20)
    }

    fun success() {
        progress = 100
        invalidate()
        handler.removeCallbacksAndMessages(null)
    }

    override fun run() {
        progress += 1
        if (progress > 98) progress = 98
        invalidate()
        when (progress) {
            in 0..20 -> handler.postDelayed(this, 20)
            in 20..45 -> handler.postDelayed(this, 30)
            in 45..60 -> handler.postDelayed(this, 40)
            in 60..98 -> handler.postDelayed(this, 50)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas)
        drawProgress(canvas)
        drawProgressTv(canvas)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroyed(owner: LifecycleOwner) {
        handler.removeCallbacksAndMessages(null)
    }

    protected fun drawText(canvas: Canvas,x:Float,y:Float,text:String){
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.textSize = progressTvSize.toFloat()
        textPaint.color = progressTvColor
        canvas.drawCenterText(x, y, text, textPaint)
    }

    abstract fun drawBackground(canvas: Canvas)
    abstract fun drawProgress(canvas: Canvas)
    abstract fun drawProgressTv(canvas: Canvas)


    init {
        attrs?.run {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressView)
            mBackgroundColor = typedArray.getColor(R.styleable.ProgressView_progress_background_color, mBackgroundColor)
            progressColor = typedArray.getColor(R.styleable.ProgressView_progress_progress_color, progressColor)
            progressTvColor = typedArray.getColor(R.styleable.ProgressView_progress_progress_tv_color, progressTvColor)
            progressTvSize = typedArray.getDimensionPixelSize(R.styleable.ProgressView_progress_progress_tv_size, progressTvSize)
            progress = typedArray.getInt(R.styleable.ProgressView_progress_progress_number, 0)
        }
    }
}