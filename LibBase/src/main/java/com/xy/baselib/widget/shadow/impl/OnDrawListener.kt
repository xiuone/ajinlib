package com.xy.baselib.widget.shadow.impl

import android.graphics.Canvas

interface OnDrawListener : OnSizeChangeListener {
    fun onDraw(canvas: Canvas?)
}