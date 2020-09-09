package com.jianbian.baselib.view

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import com.jianbian.baselib.R

class RoundLayout (context: Context,attrs: AttributeSet):FrameLayout(context,attrs){
    private var redis = 0
    private var path: Path? = null
    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.RoundLayout)
        redis = array.getDimensionPixelSize(R.styleable.RoundLayout_aj_round_layout_radius, 0)
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (path == null) path = Path()
        path?.reset()
        path?.addRoundRect(RectF(0F, 0F, measuredWidth.toFloat(), measuredHeight.toFloat()), redis.toFloat(), redis.toFloat(), Path.Direction.CW)
        canvas.drawFilter = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        if (Build.VERSION.SDK_INT >= 28) {
            canvas.clipPath(path!!)
        } else {
            canvas.clipPath(path!!, Region.Op.REPLACE)
        }
        super.dispatchDraw(canvas)
    }
}