package com.xy.baselib.widget.recycler.listener

import android.view.View
import com.xy.baselib.widget.recycler.adapter.BaseViewHolder

interface OnChildItemLongClickListener<T>{
    fun onItemChildLongClick(view:View,data:T, holder: BaseViewHolder):Boolean
}