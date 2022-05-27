package com.xy.baselib.widget.recycler.listener

import android.view.View
import com.xy.baselib.widget.recycler.adapter.BaseViewHolder

interface OnItemLongClickListener<T>{
    fun onItemLongClick(view:View,data:T,holder: BaseViewHolder?):Boolean
}