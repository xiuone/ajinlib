package com.xy.baselib.widget.msg

import android.graphics.PointF

interface OnBubbleTouchListener {
    //回弹
    fun springBack()

    //爆炸动画
    fun dismiss(pointF: PointF?)
}