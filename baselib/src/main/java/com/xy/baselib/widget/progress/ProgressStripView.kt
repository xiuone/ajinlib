package com.xy.baselib.widget.progress

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import com.xy.baselib.R
import com.xy.utils.Logger
import kotlin.math.abs
import kotlin.math.min

open class ProgressStripView  @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ProgressBaseView(context, attrs, defStyleAttr) {

    override fun drawBackground(canvas: Canvas) {
        val rectF = RectF(startLeft(), startTop(), startRight(), startBottom())
        val radius = min(abs(startRight()-startLeft()), abs(startBottom()-startTop())) / 2
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = mBackgroundColor
        canvas.drawRoundRect(rectF, radius, radius, paint)
    }

    override fun drawProgress(canvas: Canvas) {
        val rectF = RectF()
        val radius = min(width, height) / 2
        rectF.top = startTop()
        rectF.left = startLeft()
        rectF.bottom = startBottom()
        rectF.right = startLength() + (startRight() - startLength()) * progress / 100f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = progressColor
        canvas.drawRoundRect(rectF, radius.toFloat(), radius.toFloat(), paint)
    }

    override fun drawProgressTv(canvas: Canvas) {
        val centerX = ((startRight()-startLeft() - startLength()) * progress / 100f).toInt() + height / 2
        drawText(canvas,centerX.toFloat(),height/2F,"$progress%")
    }


    open fun startLeft():Float = 0F//左边的开始距离
    open fun startRight():Float = width.toFloat()//右边的开始距离
    open fun startTop():Float = 0F//上边的开始距离
    open fun startBottom():Float = height.toFloat()//下边的开始距离
    open fun startLength():Float = startBottom() - startTop()// 开始的时候   百分比为0的时候的长度
}