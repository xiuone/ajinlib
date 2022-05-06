package com.xy.baselib.widget.tab.listener

import androidx.annotation.DrawableRes

interface OnTabDrawItemListener :OnTabTextItemListener{
    @DrawableRes
    fun onUnSelectDrawRes():Int
    fun onSelectDrawRes():Int
}