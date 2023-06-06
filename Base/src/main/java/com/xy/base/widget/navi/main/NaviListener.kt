package com.xy.base.widget.navi.main

import android.graphics.Color
import androidx.annotation.DrawableRes
import com.xy.base.R

interface NaviListener {
    @DrawableRes
    fun drawRes():Int = R.drawable.bg_transparent
    fun textColorRes():Int = Color.TRANSPARENT
    fun titleStr():String
}