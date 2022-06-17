package com.xy.baselib.widget.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.xy.baselib.widget.recycler.AdapterViewType

abstract class RecyclerSingleAdapter<T>(@LayoutRes private val layoutId:Int) : RecyclerBaseAdapter<T>() {

    override fun onItemCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return BaseViewHolder(view)
    }

    override fun onItemViewType(position: Int): Int {
        return AdapterViewType.VIEW_TYPE_CONTENT
    }
}