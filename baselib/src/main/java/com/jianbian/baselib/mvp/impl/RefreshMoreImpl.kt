package com.jianbian.baselib.mvp.impl

import android.view.View
import com.jianbian.baselib.adapter.BaseRecyclerAdapter

interface RefreshMoreImpl{
    fun getData(page:Int,pageSize:Int){}
    fun onItemClick(adapter: BaseRecyclerAdapter<*>, view: View, position: Int){}
    fun onItemChildClick(adapter: BaseRecyclerAdapter<*>, view: View, position: Int){}
}