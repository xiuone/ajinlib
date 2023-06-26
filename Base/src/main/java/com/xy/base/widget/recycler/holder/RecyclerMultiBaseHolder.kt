package com.xy.base.widget.recycler.holder

import android.view.View
import com.xy.base.widget.recycler.adapter.RecyclerMultiAdapter
import com.xy.base.widget.recycler.adapter.RecyclerMultiListener


abstract class RecyclerMultiBaseHolder(itemView:View, val adapter: RecyclerMultiAdapter?) : BaseViewHolder(itemView){
     open fun onBindViewHolder(data: RecyclerMultiListener, position: Int,exp:Any?){}
     open fun onBindViewHolder(data: RecyclerMultiListener, position: Int){}
}