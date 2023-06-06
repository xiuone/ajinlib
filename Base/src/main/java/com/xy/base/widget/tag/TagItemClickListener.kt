package com.xy.base.widget.tag

import android.view.View


interface TagItemClickListener<T> {
    fun onTagItemClicked(itemView:View,item:T,tagViewHolder: TagViewHolder,position:Int){}
    fun onTagChildItemClicked(itemView:View,item:T,tagViewHolder: TagViewHolder,position:Int){}
}