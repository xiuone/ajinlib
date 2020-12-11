package com.jianbian.baselib.view

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import com.jianbian.baselib.R

class RoundLayout (context: Context,attrs: AttributeSet):FrameLayout(context,attrs){
    private var redis = 0
    private var intercept = false
    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.RoundLayout)
        redis = array.getDimensionPixelSize(R.styleable.RoundLayout_aj_round_layout_radius, 0)
        intercept = array.getBoolean(R.styleable.RoundLayout_aj_intercept,false)
    }
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return intercept
    }

    override fun dispatchDraw(canvas: Canvas) {
        val path = Path()
        path?.reset()
        path?.addRoundRect(RectF(0F, 0F, measuredWidth.toFloat(), measuredHeight.toFloat()), redis.toFloat(), redis.toFloat(), Path.Direction.CW)
        canvas.drawFilter = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.clipPath(path!!)
        super.dispatchDraw(canvas)
    }
}