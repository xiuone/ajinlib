package com.xy.base.widget.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.xy.base.widget.recycler.holder.BaseViewHolder

abstract class RecyclerSingleAdapter<T>(@LayoutRes private val layoutId:Int) : RecyclerBaseAdapter<T>() {

    override fun onItemCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return BaseViewHolder(view)
    }

    override fun onItemViewType(position: Int): Int  = VIEW_TYPE_CONTENT

}