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
import com.jianbian.baselib.mvp.impl.OnChildItemLongClickListener
import com.jianbian.baselib.mvp.impl.OnItemClickListener
import com.jianbian.baselib.mvp.impl.OnItemLongClickListener
import com.jianbian.baselib.utils.setOnClick

abstract class BaseRecyclerAdapter <T>(@LayoutRes val layoutResId:Int =R.layout.dialog_common_load): RecyclerView.Adapter<ViewHolder>(){
    val data: MutableList<T> = ArrayList<T>()
    var headView:View?=null
    private val headType:Int = -10000
    var onItemClickListener:OnItemClickListener?=null
    var onItemLongClickListener:OnItemLongClickListener?=null
    var onChildItemClickListener:OnChildItemClickListener?=null
    var onChildItemLongClickListener:OnChildItemLongClickListener?=null
    var itemClickChilds = ArrayList<@IdRes Int>()
    var itemLongClickChilds = ArrayList<@IdRes Int>()

    fun addHead(view:View){
        var haveHead = false
        if (headView != null)
            haveHead = true
        headView = view
        notifyItemInserted(1)
    }

    fun removeHead(){
        if (headView != null)
            notifyItemRemoved(0)
        headView = null
    }

    open fun addData(item: T?) {
        if (item == null)return
        data.add(item)
        if (data.size >1 ){
            notifyItemInserted(getHeadSize()+data.size)
        }else{
            notifyDataSetChanged()
        }
    }
    fun getHeadSize():Int = if (headView == null) 0 else 1

    open fun addData(position:Int,item: T?) {
        if (item != null) {
            if (position < data.size){
                data.add(position, item)
                notifyItemInserted(getHeadSize()+position)
            }else{
                addData(item)
            }
        }
    }

    open fun addData(data: List<T>?) {
        if (data != null) {
            this.data.addAll(data)
            notifyItemRangeInserted(getHeadSize()+this.data.size - data.size, data.size)
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
        if (position in getHeadSize()..(getHeadSize()+data.size)){
            return data[position-getHeadSize()]
        }
        return null
    }

    open fun remove(position: Int) {
        if (position in getHeadSize()..(getHeadSize()+data.size)){
            data.removeAt(position-getHeadSize())
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
            itemClickChilds.add(viewId)
        }
    }
    fun addChildLongClickViewIds(onChildItemLongClickListener :OnChildItemLongClickListener,@IdRes vararg viewIds: Int) {
        this.onChildItemLongClickListener = onChildItemLongClickListener
        for (viewId in viewIds) {
            itemLongClickChilds.add(viewId)
        }
    }

    //获取数据的数量
    override fun getItemCount(): Int {
        return getHeadSize()+data.size
    }

    override fun getItemViewType(position: Int): Int{
        if (position<getHeadSize())
            return headType
        else return itemType(position-getHeadSize())
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == headType && headView != null){
            return ViewHolder(headView!!,this)
        }
        return onCreateMineViewHolder(viewGroup, viewType)
    }

    override fun onBindViewHolder(@NonNull viewHolder: ViewHolder, position: Int) {
        if (position in getHeadSize()..(getHeadSize()+data.size)) {
            val data: T = data[position-getHeadSize()]
            viewHolder.setItemClickListener(onItemClickListener)
            viewHolder.setItemLongClickListener(onItemLongClickListener)
            viewHolder.setChildItemClickListener(itemClickChilds,onChildItemClickListener)
            viewHolder.setChildItemLongClickListener(itemLongClickChilds,onChildItemLongClickListener)
            convert(viewHolder.itemView.context,viewHolder, data, position)
        }
    }

    abstract fun convert(context:Context,viewHolder: ViewHolder,item: T,position: Int)

    open fun itemType(position: Int):Int = position

    open fun onCreateMineViewHolder(@NonNull viewGroup: ViewGroup, viewType: Int):ViewHolder{
        val view: View = LayoutInflater.from(viewGroup.context).inflate(layoutResId, viewGroup, false)
        return ViewHolder(view,this)
    }
}