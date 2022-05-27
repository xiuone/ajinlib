package com.xy.baselib.widget.common.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View

class BottomLineDrawImpl(view: View,context: Context) : CommonDrawImpl(view, context){
    override fun onDraw(canvas: Canvas?) {
        val backgroundRectF = RectF(0F,0F,view.width.toFloat(),view.height.toFloat())
        val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        backgroundPaint.color = if (touch && pressBackgroundColor != commonBackgroundColor) pressBackgroundColor else if (selected) focusBackgroundColor else commonBackgroundColor
        canvas?.drawRoundRect(backgroundRectF,radius,radius,backgroundPaint)

        val stokeRectF = RectF(0F,view.height- stokeSize,view.width.toFloat(),view.height.toFloat())
        val stokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        stokePaint.color = if (touch && pressStokeColor != stokeColor) pressStokeColor else if (selected) focusStokeColor else stokeColor
        canvas?.drawRoundRect(stokeRectF,radius,radius,stokePaint)
    }
}