package com.xy.baselib.widget.recycler.listener

import android.view.View
import com.xy.baselib.widget.recycler.adapter.BaseViewHolder

interface OnChildItemClickListener<T>{
    fun onItemChildClick(view:View,data:T,holder: BaseViewHolder?)
}