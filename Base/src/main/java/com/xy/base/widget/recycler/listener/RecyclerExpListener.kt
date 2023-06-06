package com.xy.base.widget.recycler.listener

import com.xy.base.widget.recycler.holder.BaseViewHolder

interface RecyclerExpListener<T> {
    fun onBindViewHolder(holder: BaseViewHolder, data: T, position: Int)
}