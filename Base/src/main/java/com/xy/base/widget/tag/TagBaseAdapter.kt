package com.xy.base.widget.tag

import android.view.View
import com.xy.base.utils.exp.setOnClick

abstract class TagBaseAdapter<T> :TagView.TagAdapter() {
    val data by lazy { ArrayList<T>() }
    var itemClickedListener :TagItemClickListener<T>? = null
    var itemLongClickedListener :TagItemLongClickListener<T>? = null

    private val childClickMap :HashMap<Int, TagItemClickListener<T>> by lazy {  HashMap() }
    private val childLongClickMap :HashMap<Int, TagItemLongClickListener<T>> by lazy {  HashMap() }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        if (position < data.size)return
        val item = data[position]
        onBindViewHolder(holder,item,position)
        if (itemClickedListener != null){
            holder.itemView.setOnClickListener{
                itemClickedListener?.onTagItemClicked(it,item,holder,position)
            }
        }
        if (itemLongClickedListener != null){
            holder.itemView.setOnLongClickListener {
                itemLongClickedListener?.onTagLongItemClicked(it,item,holder,position) == true
            }
        }
        for (entries in childClickMap.entries){
            holder.getView<View>(entries.key)?.setOnClick{
                entries.value.onTagChildItemClicked(it,item,holder,position)
            }
        }
        for (entries in childLongClickMap.entries){
            holder.getView<View>(entries.key)?.setOnLongClickListener{
                entries.value.onTagChildLongItemClicked(it,item,holder,position)
            }
        }
    }

    fun addChildClicked(listener: TagItemClickListener<T>?, vararg viewIds: Int?): TagBaseAdapter<T> {
        synchronized(this){
            if (listener == null)return  this
            for (item in viewIds){
                addChildClicked(item,listener)
            }
            return this
        }
    }

    fun addChildClicked(viewId: Int?,listener: TagItemClickListener<T>?): TagBaseAdapter<T> {
        synchronized(this){
            if (listener == null)return  this
            if (viewId != null){
                childClickMap[viewId] = listener
            }
            return this
        }
    }



    fun addChildLongClicked(listener: TagItemLongClickListener<T>?, vararg viewIds: Int?): TagBaseAdapter<T> {
        synchronized(this){
            if (listener == null)return  this
            for (item in viewIds){
                addChildLongClicked(item,listener)
            }
            return this
        }
    }

    fun addChildLongClicked(viewId: Int?,listener: TagItemLongClickListener<T>?): TagBaseAdapter<T> {
        synchronized(this){
            if (listener == null)return  this
            if (viewId != null){
                childLongClickMap[viewId] = listener
            }
            return this
        }
    }


    open fun setNewData(data:MutableList<T>){
        synchronized(this){
            this.data.clear()
            this.data.addAll(data)
            notifyDataSetChangedRange(0,getItemCount())
        }
    }

    open fun addItem(item:T){
        synchronized(this){
            this.data.add(item)
            notifyDataSetChangedRange(getItemCount()-1,getItemCount())
        }
    }

    open fun remove(position:Int){
        synchronized(this){
            if (this.data.size > position){
                this.data.removeAt(position)
                notifyItemRemoveRange(position,1)
            }
        }
    }

    open fun remove(position:Int,count:Int){
        synchronized(this){
            for (index in position until (position+count)){
                if (this.data.size < index){
                    this.data.removeAt(index)
                    notifyItemRemoveRange(index,1)
                }
            }
        }
    }



    fun getCurrentData() = data

    override fun getItemViewType(position: Int): Int = 0

    override fun getItemCount(): Int = data.size

    abstract fun onBindViewHolder(holder: TagViewHolder,item:T,position: Int)
}