package xy.xy.base.widget.shadow.impl

import android.graphics.Canvas

interface OnDrawListener : OnSizeChangeListener {
    fun onDraw(canvas: Canvas?)
    fun onClipPathDraw(canvas: Canvas?)
    fun onDrawStoke(canvas: Canvas?)
}