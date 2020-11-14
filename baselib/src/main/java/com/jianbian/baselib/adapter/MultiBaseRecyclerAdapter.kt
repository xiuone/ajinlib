package com.jianbian.baselib.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import com.jianbian.baselib.mvp.impl.MultiImpl

abstract class MultiBaseRecyclerAdapter<T :MultiImpl>():BaseRecyclerAdapter<T>() {
    private val layoutResHashMap = HashMap<Int,@LayoutRes Int>()
    protected fun addLayout(type:Int,@LayoutRes res:Int){
        layoutResHashMap[type] = res
    }

    override fun onCreateMineViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val res = layoutResHashMap[viewType]?:layoutResId
        val view: View = LayoutInflater.from(viewGroup.context).inflate(res, viewGroup, false)
        return ViewHolder(view,this,onItemClickListener,onChildItemClickListener,childs)
    }


    override fun itemType(position: Int): Int {
        val item = data[position-head.size]
        return item.getType()
    }
}