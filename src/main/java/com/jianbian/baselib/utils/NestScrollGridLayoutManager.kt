package com.jianbian.baselib.utils

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager

class NestScrollGridLayoutManager (context: Context?, number:Int,val nestScroll:Boolean = true): GridLayoutManager(context,number) {
    override fun canScrollHorizontally(): Boolean {
        return nestScroll
    }

    override fun canScrollVertically(): Boolean {
        return nestScroll
    }
}