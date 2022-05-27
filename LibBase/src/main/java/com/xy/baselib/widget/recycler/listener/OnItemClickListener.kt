package com.xy.baselib.widget.recycler.listener

import android.view.View
import com.xy.baselib.widget.recycler.adapter.BaseViewHolder

interface OnItemClickListener<T>{
    fun onItemClick(view:View,data:T, holder: BaseViewHolder?)
}