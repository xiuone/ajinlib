package com.xy.baselib.widget.recycler.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.xy.db.MultiEntry
import com.xy.baselib.widget.recycler.listener.*

abstract class RecyclerBaseAdapter<T> :RecyclerView.Adapter<BaseViewHolder>(){
    private val data :List<T> by lazy { ArrayList() }

    var itemClickListener: OnItemClickListener<T>?=null
        set(value) {
            field = value
        }
    var itemLongListener: OnItemLongClickListener<T>?=null
    var itemTouchListener: OnItemTouchListener<T> ?= null

    private val childClickMap :HashMap<Int,OnChildItemClickListener<T>> by lazy {  HashMap() }
    private val childLongClickMap :HashMap<Int,OnChildItemLongClickListener<T>> by lazy {  HashMap() }

    private val childTouchMap :HashMap<Int,OnItemTouchListener<T>> by lazy {  HashMap() }

    fun addChildClicked(viewIds: Int,listener: OnChildItemClickListener<T>?):RecyclerBaseAdapter<T>{
        if (listener == null)return this
        childClickMap[viewIds] = listener;
        return this
    }

    fun addChildLongClicked(viewIds: Int,listener: OnChildItemLongClickListener<T>?):RecyclerBaseAdapter<T>{
        if (listener == null)return this
        childLongClickMap[viewIds] = listener;
        return this
    }

    fun addChildTouch(viewIds: Int,listener: OnItemTouchListener<T>?):RecyclerBaseAdapter<T>{
        if (listener == null)return this
        childTouchMap[viewIds] = listener;
        return this;
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (position< data.size) {
            val data = data[position];
            if (itemClickListener != null) {
                holder.itemView.setOnClickListener {
                    itemClickListener?.onItemClick(it,data, holder)
                }
            }
            if (itemLongListener != null) {
                holder.itemView.setOnLongClickListener {
                    return@setOnLongClickListener itemLongListener?.onItemLongClick(it, data, holder)?:false
                }
            }

            if (itemTouchListener != null) {
                holder.itemView.setOnTouchListener { v, event ->
                    return@setOnTouchListener itemTouchListener?.onTouch(v, data, event) ?: false
                }
            }
            for ((key,value) in childClickMap){
                val view :View? = holder.getView(key)
                view?.setOnClickListener{
                    value.onItemChildClick(it,data, holder)
                }
            }
            for ((key,value) in childLongClickMap){
                val view :View? = holder.getView(key)
                view?.setOnLongClickListener{
                    return@setOnLongClickListener value.onItemChildLongClick(it,data, holder)
                }
            }

            for ((key,value) in childTouchMap){
                val view :View? = holder.getView(key)
                view?.setOnTouchListener { v, event ->
                    return@setOnTouchListener value.onTouch(v, data, event)
                }
            }
            onBindViewHolder(holder,data, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position < data.size){
            val data  = data[position];
            if (data is MultiEntry) {
                val viewType = data.getViewType();
                if (viewType > 0 )
                    return viewType;
            }
        }
        return 1
    }


    abstract fun onBindViewHolder(holder: BaseViewHolder,data:T, position: Int)

}