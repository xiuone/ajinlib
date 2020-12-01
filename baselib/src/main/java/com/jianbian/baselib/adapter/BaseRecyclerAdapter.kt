package com.jianbian.baselib.adapter

import android.content.Context
import android.util.Log
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
    private var headView:View?=null
    private var entryView:View?=null
    private var footView:View?=null
    private val headType:Int = -10000
    private val entryType:Int = -10001
    private val footType:Int = -10002
    private var loadFirst = false;
    var onItemClickListener:OnItemClickListener?=null
    var onItemLongClickListener:OnItemLongClickListener?=null
    var onChildItemClickListener:OnChildItemClickListener?=null
    var onChildItemLongClickListener:OnChildItemLongClickListener?=null
    var itemClickChilds = ArrayList<@IdRes Int>()
    var itemLongClickChilds = ArrayList<@IdRes Int>()

    fun addHead(view:View){
        headView = view
        headView?.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        notifyItemInserted(1)
    }

    fun removeHead(){
        var status = headView != null
        headView = null
        if (status) notifyItemRemoved(0)
    }

    fun getHeadSize():Int = if (headView == null) 0 else 1

    fun addFoot(view:View){
        footView = view
        footView?.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        notifyItemInserted(getHeadSize()+data.size+getFootSize())
    }

    fun removeFoot(){
        var status = footView != null
        footView = null
        if (status) notifyItemRemoved(getHeadSize()+data.size+getEntrySize())
    }

    fun getFootSize():Int = if (footView == null) 0 else 1

    fun setEntryView(view: View?){
        entryView = view
        entryView?.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        if (data.size<0)
            notifyItemInserted(getHeadSize()+getEntrySize())
    }

    fun getEntrySize():Int = if (entryView == null) 0 else 1

    open fun addData(item: T?) {
        if (item == null)return
        loadFirst = true
        data.add(item)
        if (data.size >1){
            notifyItemInserted(getHeadSize()+data.size)
        }else{
            notifyDataSetChanged()
        }
    }

    open fun addData(position:Int,item: T?) {
        loadFirst = true
        if (item != null) {
            if (position < data.size){
                data.add(position, item)
                if (data.size == 1){
                    notifyItemChanged(getHeadSize())
                }else
                    notifyItemInserted(getHeadSize()+position)
            }else{
                addData(item)
            }
        }
    }

    open fun addData(data: List<T>?) {
        loadFirst = true
        if (data != null) {
            this.data.addAll(data)
            notifyItemRangeInserted(getHeadSize()+this.data.size - data.size, data.size)
        }
    }

    open fun setNewData(data: List<T>?) {
        loadFirst = true
        this.data.clear()
        if (data != null) {
            this.data.addAll(data)
            notifyDataSetChanged()
        }
    }

    open fun remove(position: Int) {
        loadFirst = true
        if (position in getHeadSize() until (getHeadSize()+data.size)){
            data.removeAt(position-getHeadSize())
            notifyItemRemoved(position)
        }
    }

    fun getItem(position:Int):T?{
        if (position in getHeadSize() until (getHeadSize()+data.size)){
            return data[position-getHeadSize()]
        }
        return null
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
        return getHeadSize()+getFootSize()+(if (data.size>0) data.size else if (loadFirst) getEntrySize() else 0)
    }

    override fun getItemViewType(position: Int): Int{
        if (position<getHeadSize())
            return headType
        else if (position<getHeadSize()+data.size)
            return itemType(position-getHeadSize())
        else if (position < getHeadSize()+getEntrySize())
            return entryType
        else return footType
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == headType && headView != null)  return ViewHolder(headView!!,this)
        else if (viewType == entryType && entryView != null)  return ViewHolder(entryView!!,this)
        else if (viewType == footType && footView != null)  return ViewHolder(footView!!,this)
        return onCreateMineViewHolder(viewGroup, viewType)
    }

    override fun onBindViewHolder(@NonNull viewHolder: ViewHolder, position: Int) {
        if (position in getHeadSize() until (getHeadSize()+data.size)) {
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