package com.xy.base.widget.recycler.listener

import android.view.View
import com.xy.base.widget.recycler.holder.BaseViewHolder

interface OnItemLongClickListener<T>{
    fun onItemLongClick(view:View,data:T,holder: BaseViewHolder?):Boolean
}