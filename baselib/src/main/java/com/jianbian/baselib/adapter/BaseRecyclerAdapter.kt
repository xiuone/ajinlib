package com.jianbian.baselib.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.j256.ormlite.stmt.query.In
import com.jianbian.baselib.R
import com.jianbian.baselib.mvp.impl.OnChildItemClickListener
import com.jianbian.baselib.mvp.impl.OnItemClickListener
import com.jianbian.baselib.utils.setOnClick

abstract class BaseRecyclerAdapter <T>(@LayoutRes val layoutResId:Int =R.layout.dialog_common_load): RecyclerView.Adapter<ViewHolder>(){
    var data: MutableList<T> = ArrayList<T>()
    var onItemClickListener:OnItemClickListener?=null
    var onChildItemClickListener:OnChildItemClickListener?=null
    var childs = ArrayList<@IdRes Int>()

    open fun addData(item: T?) {
        if (item == null)return
        data.add(item)
        if (data.size >1 ){
            notifyItemInserted(data.size)
        }else{
            notifyDataSetChanged()
        }
    }

    open fun addData(position:Int,item: T?) {
        if (item != null) {
            if (position < data.size){
                data.add(position, item)
                notifyItemInserted(position)
            }else{
                addData(item)
            }
        }
    }

    open fun addData(data: List<T>?) {
        if (data != null) {
            this.data.addAll(data)
            notifyItemRangeInserted(this.data.size - data.size, data.size)
        }
    }

    open fun setNewData(data: List<T>?) {
        this.data.clear()
        if (data != null) {
            this.data.addAll(data)
            notifyDataSetChanged()
        }
    }

    fun getItem(position:Int):T?{
        if (position < data.size && position>=0)
            return data[position]
        return null
    }

    open fun remove(position: Int) {
        if (position>=0 && position < data.size){
            data.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    /**
     * 获取View
     */
    fun<VI : View> getViewByPostion(recyclerView: RecyclerView?,position: Int,viewId:Int):VI?{
        if (recyclerView == null)
            return null
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
        if (viewHolder == null || viewHolder !is ViewHolder){
            return null
        }else{
            return viewHolder.getView<VI>(viewId)
        }
    }



    fun addChildClickViewIds(onChildItemClickListener :OnChildItemClickListener,@IdRes vararg viewIds: Int) {
        this.onChildItemClickListener = onChildItemClickListener
        for (viewId in viewIds) {
            childs.add(viewId)
        }
    }

    //获取数据的数量
    override fun getItemCount(): Int {
        return data.size
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(layoutResId, viewGroup, false)
        return ViewHolder(view,this,onItemClickListener,onChildItemClickListener,childs)
    }

    override fun onBindViewHolder(@NonNull viewHolder: ViewHolder, position: Int) {
        if (position < data.size) {
            val data: T = data[position]
            convert(viewHolder.itemView.context,viewHolder, data, position)
        }
    }

    abstract fun convert(context:Context,viewHolder: ViewHolder,item: T,position: Int)
}