package com.xy.baselib.utils

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NestLinearLayoutManager (context: Context?, @RecyclerView.Orientation orientation:Int ,
                                reverseLayout:Boolean, val nestScroll:Boolean = true)
    : LinearLayoutManager(context,orientation,reverseLayout) {
    override fun canScrollHorizontally(): Boolean {
        return nestScroll
    }

    override fun canScrollVertically(): Boolean {
        return nestScroll
    }
}