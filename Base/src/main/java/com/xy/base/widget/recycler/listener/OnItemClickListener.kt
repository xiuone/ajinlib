package com.xy.base.widget.recycler.listener

import android.view.View
import com.xy.base.widget.recycler.holder.BaseViewHolder

interface OnItemClickListener<T>{
    fun onItemClick(view:View,data:T, holder: BaseViewHolder?)
}