package com.xy.base.widget.recycler.listener

import android.view.View
import com.xy.base.widget.recycler.holder.BaseViewHolder

interface OnChildItemLongClickListener<T>{
    fun onItemChildLongClick(view:View,data:T, holder: BaseViewHolder?):Boolean
}