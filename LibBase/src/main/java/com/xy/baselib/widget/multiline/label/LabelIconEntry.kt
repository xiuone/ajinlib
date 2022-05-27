package com.xy.baselib.widget.multiline.label

import androidx.annotation.DrawableRes

interface LabelIconEntry :LabelEntry{
    @DrawableRes
    fun onIcon():Int
}