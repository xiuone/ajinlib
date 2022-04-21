package com.xy.baselib.widget.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.xy.db.MultiEntry
import com.xy.utils.getSpace

abstract class RecyclerMultiAdapter<T : MultiEntry>() : RecyclerBaseAdapter<T>() {
    private val hashMap = HashMap<Int, Int>()

    fun addTypeView(type:Int,@LayoutRes layoutRes: Int){
        if (type >0 ){
            hashMap[type] = layoutRes;
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val layoutRes = hashMap[viewType]
        return if (layoutRes != null){
            BaseViewHolder(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false))
        }else{
            BaseViewHolder(parent.context.getSpace());
        }
    }
}