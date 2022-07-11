package com.xy.baselib.widget.recycler.adapter

import android.annotation.SuppressLint
import android.view.View
import com.xy.baselib.widget.recycler.listener.*

abstract class RecyclerBaseAdapter<T> :RecyclerAdapterWrapper<BaseViewHolder>(){
    val data :MutableList<T> by lazy { ArrayList() }

    var itemClickListener: OnItemClickListener<T>?=null
    var itemLongListener: OnItemLongClickListener<T>?=null
    var itemTouchListener: OnItemTouchListener<T> ?= null

    private val childClickMap :HashMap<Int,OnChildItemClickListener<T>> by lazy {  HashMap() }
    private val childLongClickMap :HashMap<Int,OnChildItemLongClickListener<T>> by lazy {  HashMap() }

    private val childTouchMap :HashMap<Int,OnItemTouchListener<T>> by lazy {  HashMap() }

    fun addChildClicked(listener: OnChildItemClickListener<T>?,vararg viewIds: Int,):RecyclerBaseAdapter<T>{
        synchronized(this){
            for (item in viewIds){
                addChildClicked(item,listener)
            }
            return this
        }
    }
    fun addChildClicked(viewIds: Int,listener: OnChildItemClickListener<T>?):RecyclerBaseAdapter<T>{
        synchronized(this){
            if (listener == null)return this
            childClickMap[viewIds] = listener;
            return this
        }
    }

    fun addChildLongClicked(viewIds: Int,listener: OnChildItemLongClickListener<T>?):RecyclerBaseAdapter<T>{
        synchronized(this){
            if (listener == null)return this
            childLongClickMap[viewIds] = listener;
            return this
        }
    }

    fun addChildTouch(viewIds: Int,listener: OnItemTouchListener<T>?):RecyclerBaseAdapter<T>{
        synchronized(this){
            if (listener == null)return this
            childTouchMap[viewIds] = listener;
            return this;
        }
    }


    fun setNewData(data: MutableList<T>?){
        synchronized(this){
            this.data.clear()
            data?.run {
                this@RecyclerBaseAdapter.data.addAll(this)
            }
            notifyDataSetChanged()
        }
    }

    fun addData(data: MutableList<T>?){
        synchronized(this){
            if (this.data.size<=0) {
                setNewData(data)
            }else if (data != null){
                val start: Int = this.data.size
                this@RecyclerBaseAdapter.data.addAll(data)
                notifyItemRangeChanged(start, this.data.size)
            }
        }

    }

    fun addItem(data: T){
        synchronized(this){
            if (this.data.size<=0) {
                val list = ArrayList<T>()
                list.add(data)
                setNewData(list)
            }else if (data != null){
                val start: Int = this.data.size
                this@RecyclerBaseAdapter.data.add(data)
                notifyItemRangeChanged(start, this.data.size)
            }
        }
    }

    fun addItem(data: T,position: Int){
        synchronized(this){
            if (position>= this.data.size){
                addItem(data)
            }else{
                this@RecyclerBaseAdapter.data.add(position,data)
                notifyItemRangeChanged(heardMap.size+position, heardMap.size+this.data.size)
            }
        }

    }

    fun removeItem(position: Int):T?{
        synchronized(this){
            var item: T? = null
            if (position < this.data.size) {
                item = data.removeAt(position)
                notifyItemRemoved(position+heardMap.size)
                notifyItemRangeChanged(heardMap.size+position, heardMap.size+this.data.size - position)
            }
            if (this.data.size<0){
                notifyDataSetChanged()
            }
            return item
        }
    }

    fun removeItems(index: Int, count: Int) {
        synchronized(this){
            if (index < data.size) {
                val last = index + count - 1
                for (i in last downTo index) {
                    data.removeAt(i)
                }
                notifyItemRangeRemoved(heardMap.size+index, count)
                notifyItemRangeChanged(heardMap.size+index, heardMap.size+data.size - index)
                if (this.data.size<0){
                    notifyDataSetChanged()
                }
            }
        }
    }


    override fun onItemContentCount(): Int = data.size

    @SuppressLint("ClickableViewAccessibility")
    override fun onItemBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (position< data.size) {
            val data = data[position]
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


    abstract fun onBindViewHolder(holder: BaseViewHolder,data:T, position: Int)

}