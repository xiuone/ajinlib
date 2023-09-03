package xy.xy.base.widget.shadow.impl

import android.graphics.Canvas

interface OnDrawExpListener {
    fun onExpDraw(canvas: Canvas?):Boolean = false
    fun onExpDrawStoke(canvas: Canvas?) :Boolean = false
}