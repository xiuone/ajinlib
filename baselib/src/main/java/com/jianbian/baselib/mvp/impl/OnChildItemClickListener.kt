package com.jianbian.baselib.mvp.impl

import android.view.View
import com.jianbian.baselib.adapter.BaseRecyclerAdapter

interface OnChildItemClickListener{
    fun onItemClick(adapter: BaseRecyclerAdapter<*> ,view:View,position:Int)
}