package com.xy.base.widget.bar.progress

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.xy.base.utils.exp.drawCenterText
import com.xy.base.utils.exp.getResColor
import com.xy.base.R

abstract class ProgressBaseView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) ,Runnable, LifecycleObserver {
    var progressListener:ProgressListener?=null
    var progressTv:TextView?=null

    protected val white by lazy { context.getResColor(R.color.white) }

    private val runHandler by lazy { Handler(Looper.getMainLooper()) }
    protected val progressBuild =
        ProgressBuild(this, attrs)


    /**
     * 更新进度后自动刷新
     */
    open fun updateRealProgress(progress: Int) {
        var progress = progress
        if (progress < 0) progress = 0
        if (progress > 100) progress = 100
        progressBuild.progress = progress
        runHandler.removeCallbacksAndMessages(null)
    }

    /**
     * 更新进度后自动刷新
     */
    open fun updateUnRealProgress(progress: Int) {
        var progress = progress
        if (progress < 0) progress = 0
        if (progress > 100) progress = 100
        progressBuild.progress = progress
        invalidate()
        runHandler.removeCallbacksAndMessages(null)
        runHandler.postDelayed(this, 20)
    }


    fun success() {
        progressBuild.progress = 100
        invalidate()
        runHandler.removeCallbacksAndMessages(null)
    }


    override fun run() {
        progressBuild.progress = progressBuild.progress +1
        if (progressBuild.progress > 98) progressBuild.progress = 98

        invalidate()
        when (progressBuild.progress) {
            in 0..20 -> runHandler.postDelayed(this, 20)
            in 20..45 -> runHandler.postDelayed(this, 30)
            in 45..60 -> runHandler.postDelayed(this, 40)
            in 60..98 -> runHandler.postDelayed(this, 50)
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas)
        drawProgress(canvas)
        if (progressBuild.isShowTv) {
            drawProgressTv(canvas)
        }
        progressTv?.text = "${progressBuild.progress}%"
    }


    protected fun drawText(canvas: Canvas,x:Float,y:Float,text:String){
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.textSize = progressBuild.progressTvSize.toFloat()
        textPaint.color = progressBuild.progressTvColor
        canvas.drawCenterText(x, y, text, textPaint)
    }

    abstract fun drawBackground(canvas: Canvas)
    abstract fun drawProgress(canvas: Canvas)
    abstract fun drawProgressTv(canvas: Canvas)
    open fun defaultShowTv():Boolean = true

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        runHandler.removeCallbacksAndMessages(null)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroyed(owner: LifecycleOwner) {
        runHandler.removeCallbacksAndMessages(null)
    }

    interface ProgressListener{
        fun onProgressCallBack(progress:Int)
    }
}