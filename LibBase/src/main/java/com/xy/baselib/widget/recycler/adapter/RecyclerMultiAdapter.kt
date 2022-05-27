package com.xy.baselib.widget.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.xy.baselib.mode.AdapterViewType
import com.xy.baselib.mode.MultiEntry
import com.xy.baselib.exp.getSpace

abstract class RecyclerMultiAdapter<T : MultiEntry>() : RecyclerBaseAdapter<T>() {
    private val hashMap = HashMap<Int, Int>()

    fun addTypeView(type:Int,@LayoutRes layoutRes: Int){
        if (type >0 ){
            hashMap[type] = layoutRes;
        }
    }

    override fun onItemCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val layoutRes = hashMap[viewType]
        return if (layoutRes != null){
            BaseViewHolder(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false))
        }else{
            BaseViewHolder(parent.context.getSpace());
        }
    }

    override fun onItemViewType(position: Int): Int {
        if (position < data.size){
            val data  = data[position];
            val viewType = data.getViewType();
            if (viewType > 0 )
                return viewType
        }
        return AdapterViewType.VIEW_TYPE_SPACE
    }
}