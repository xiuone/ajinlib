package com.jianbian.baselib.mvp.impl

import android.view.View
import com.jianbian.baselib.adapter.BaseRecyclerAdapter

interface OnChildItemClickListener{
    fun onItemChildClick(adapter: BaseRecyclerAdapter<*> ,view:View,position:Int)
}