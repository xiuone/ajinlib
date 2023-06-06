package com.xy.base.widget.recycler.adapter

import com.xy.base.widget.recycler.holder.BaseViewHolder
import com.xy.base.widget.recycler.holder.RecyclerMultiBaseHolder

abstract class RecyclerMultiAdapter : RecyclerBaseAdapter<RecyclerMultiListener>() {


    override fun onBindViewHolder(holder: BaseViewHolder, data: RecyclerMultiListener, position: Int) {
        if (holder is RecyclerMultiBaseHolder)
            holder.onBindViewHolder(data,position)
    }

    override fun onItemViewType(position: Int): Int {
        if (position < data.size){
            val item  = data[position]
            return item.onCreateRecyclerType()
        }
        return VIEW_TYPE_SPACE
    }
}