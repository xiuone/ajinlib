package com.xy.baselib.widget.bar.progress

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import com.xy.baselib.R
import com.xy.utils.getResDimension
import kotlin.math.min

class ProgressCircleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ProgressBaseView(context, attrs, defStyleAttr) {
    private var stokeWidth = 10F

    /**
     * 获取绘制区域
     */
    private fun getProgressRectF():RectF{
        val radius = min(width, height) / 2
        val left = width/2F - radius + stokeWidth/2
        val right = width/2F + radius - stokeWidth/2
        val top = height/2F - radius + stokeWidth/2
        val bottom = height/2F + radius - stokeWidth/2
        return RectF(left,top,right,bottom)
    }

    private fun getProgressPaint(color:Int):Paint{
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE;    // 只描边，不填充
        paint.strokeCap = Paint.Cap.ROUND;   // 设置圆角
        paint.isAntiAlias = true;              // 设置抗锯齿
        paint.isDither = true;                 // 设置抖动
        paint.strokeWidth = stokeWidth
        paint.color = color
        return paint
    }

    override fun drawBackground(canvas: Canvas) {
        val paint = getProgressPaint(mBackgroundColor)
        val rect = getProgressRectF()
        canvas.drawArc(rect, 0F, 360F, false, paint)
    }

    override fun drawProgress(canvas: Canvas) {
        val paint = getProgressPaint(progressColor)
        val rect = getProgressRectF()
        canvas.drawArc(rect, 275F, 360 * progress / 100F, false, paint);
    }

    override fun drawProgressTv(canvas: Canvas) {
        drawText(canvas,width/2F,height/2F,"$progress%")
    }

    init {
        attrs?.run {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressView)
            stokeWidth = typedArray.getDimension(R.styleable.ProgressView_progress_progress_circle_stoke_width, context.getResDimension(R.dimen.dp_5).toFloat())
        }
    }
}