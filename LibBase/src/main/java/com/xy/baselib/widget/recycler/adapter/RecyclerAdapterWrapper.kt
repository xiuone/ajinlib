package com.xy.baselib.widget.recycler.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.xy.baselib.exp.Logger
import com.xy.baselib.exp.getSpace
import kotlin.math.abs

abstract class RecyclerAdapterWrapper<T> : RecyclerView.Adapter<BaseViewHolder>() {
    //最大的头部数量
    private val maxHeadSize = 10000
    //空数据的时候
    private val emptyType = -100001
    //最大的底部数量
    private val maxFootSize = 10001
    //头头
    internal val heardMap :HashMap<Int,View> by lazy {  HashMap() }
    //脚脚
    private val footMap :HashMap<Int,View> by lazy { HashMap() }

    var showHaveHeadEmpty:Boolean = true
        set(value) {
            field = value
            if (onItemContentCount() == 0) {
                notifyDataSetChanged()
            }
        }

    var showHaveFootEmpty:Boolean = true
        set(value) {
            field = value
            if (onItemContentCount() == 0) {
                notifyDataSetChanged()
            }
        }

    var emptyView :View?=null
        set(value) {
            field = value
            if (onItemContentCount() == 0) {
                notifyDataSetChanged()
            }
        }
    /**
     * 添加头部
     */
    fun addHeadView(view: View?){
        if (view == null){
            Logger.e("headView not null")
            return
        }
        if (heardMap.size >= maxHeadSize){
            Logger.e("headView is Max")
            return
        }
        heardMap[-heardMap.size] = view
        notifyItemInserted(heardMap.size)
    }

    fun removeHeadView(view: View?){
        if (view == null){
            Logger.e("headView not null")
            return
        }
        for ((key, value) in heardMap) {
            if (value == view){
                heardMap.remove(key)
                notifyItemRemoved(abs(key))
                return
            }
        }
        Logger.e("headView not find")
    }

    /**
     * 添加脚脚
     */
    fun addFootView(view :View?){
        if (view == null){
            Logger.e("footView not null")
            return
        }
        if (footMap.size >= maxFootSize){
            Logger.e("footView is Max")
            return
        }
        footMap[-footMap.size + emptyType -1] = view
        notifyItemChanged(itemCount)
    }


    fun removeFootView(view: View?){
        if (view == null){ 
            Logger.e("footView not null")
            return
        }
        for ((key, value) in footMap) {
            if (value == view){
                footMap.remove(key)
                notifyItemRemoved(abs(key - emptyType +1) + heardMap.size + (if (showEmpty()) 1 else onItemContentCount()))
                return
            }
        }
        Logger.e("footView not find")
    }

    private fun isHeadOrFootOrEmpty(position: Int) =  position < heardMap.size || (position >= heardMap.size+onItemContentCount())


    private fun showEmpty():Boolean{
        var count = heardMap.size+footMap.size;
        return emptyView != null && (showHaveFootEmpty || showHaveHeadEmpty || count == 0) && onItemContentCount()<=0
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):BaseViewHolder {
        var view:View?=null
        if (viewType <= 0){
            if (viewType == emptyType && emptyView != null)
                view = emptyView
            if (view == null && heardMap.size >0){
                view = heardMap[viewType]
            }
            if (view == null && footMap.size > 0 ){
                view = footMap[viewType]
            }
            if (view == null)
                view = parent.context.getSpace()
            return BaseViewHolder(view)
        }
        return onItemCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (position >= heardMap.size && !showEmpty() && position < (heardMap.size + onItemContentCount())){
            onItemBindViewHolder(holder,position - heardMap.size)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position < heardMap.size){
            return -position
        }else if (showEmpty() && position == heardMap.size){
            return emptyType
        }else if (position < (heardMap.size + onItemContentCount())){
            return  onItemViewType(position-heardMap.size)
        }
        return emptyType - (position - heardMap.size - (if (showEmpty()) 1 else onItemContentCount()))
    }

    override fun getItemCount(): Int {
        var count = heardMap.size+footMap.size;
        if (showEmpty()){
            return count + 1
        }
        return count + onItemContentCount();
    }


    /**
     * headView refreshView  loadView entryView 都占满屏
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val manager = recyclerView.layoutManager?:return
        if (manager is GridLayoutManager){
            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (isHeadOrFootOrEmpty(position)) manager.spanCount else 1
                }
            }
        }
    }


    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        super.onViewAttachedToWindow(holder)
        val position = holder.layoutPosition
        val lp = holder.itemView.layoutParams
        if (isHeadOrFootOrEmpty(position) && lp is StaggeredGridLayoutManager.LayoutParams) {
            lp.isFullSpan = true
        }
    }
    
    
    abstract fun onItemContentCount():Int
    abstract fun onItemBindViewHolder(holder: BaseViewHolder, position: Int)
    abstract fun onItemViewType(position: Int):Int
    abstract fun onItemCreateViewHolder(parent: ViewGroup, viewType: Int):BaseViewHolder
}