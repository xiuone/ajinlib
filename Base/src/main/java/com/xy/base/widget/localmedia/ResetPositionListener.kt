package com.xy.base.widget.localmedia

import android.graphics.RectF

interface ResetPositionListener {
    fun onPosition(index:Int):IntArray
    fun onPositionRecF(index:Int):RectF
    fun onRowSize():Int
}