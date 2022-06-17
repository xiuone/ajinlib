package com.xy.baselib.widget.tabscroller

import com.xy.baselib.widget.multiline.label.LabelEntry

interface TabScrollerEntry : LabelEntry {
    fun unReadNumber():Int
}