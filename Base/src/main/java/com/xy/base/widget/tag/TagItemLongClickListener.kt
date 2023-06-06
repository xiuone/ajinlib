package com.xy.base.widget.tag

import android.view.View


interface TagItemLongClickListener<T> {
    fun onTagLongItemClicked(itemView:View,item:T,tagViewHolder: TagViewHolder,position:Int):Boolean = false
    fun onTagChildLongItemClicked(itemView:View,item:T,tagViewHolder: TagViewHolder,position:Int):Boolean = false
}