package com.xy.baselib.widget.progress

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import com.xy.baselib.R
import com.xy.utils.Logger
import kotlin.math.min

class ProgressStripView  @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ProgressBaseView(context, attrs, defStyleAttr) {

    override fun drawBackground(canvas: Canvas) {
        val rectF = RectF(0F, 0F, width.toFloat(), height.toFloat())
        val radius = min(width, height) / 2
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = mBackgroundColor
        canvas.drawRoundRect(rectF, radius.toFloat(), radius.toFloat(), paint)
    }

    override fun drawProgress(canvas: Canvas) {
        val rectF = RectF()
        val radius = min(width, height) / 2
        rectF.top = 0f
        rectF.left = 0f
        rectF.bottom = height.toFloat()
        rectF.right = height + (width - radius * 2f) * progress / 100f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = progressColor
        canvas.drawRoundRect(rectF, radius.toFloat(), radius.toFloat(), paint)
    }

    override fun drawProgressTv(canvas: Canvas) {
        val centerX = ((width - height) * progress / 100f).toInt() + height / 2
        drawText(canvas,centerX.toFloat(),height/2F,"$progress%")
    }
}