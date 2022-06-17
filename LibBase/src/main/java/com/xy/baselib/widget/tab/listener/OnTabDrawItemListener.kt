package com.xy.baselib.widget.tab.listener

import androidx.annotation.DrawableRes
import com.xy.baselib.widget.multiline.label.LabelEntry

interface OnTabDrawItemListener : LabelEntry{
    @DrawableRes
    fun onUnSelectDrawRes():Int
    fun onSelectDrawRes():Int
}