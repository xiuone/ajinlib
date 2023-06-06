package com.xy.base.utils.exp

import android.graphics.Canvas
import android.graphics.Paint

fun Canvas.drawCenterText(x:Float, y:Float, text:String,textPaint: Paint){
    textPaint.textAlign = Paint.Align.CENTER
    val fontMetrics = textPaint.fontMetrics
    val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
    val baseline = y + distance
    this.drawText(text, x, baseline, textPaint)
}
