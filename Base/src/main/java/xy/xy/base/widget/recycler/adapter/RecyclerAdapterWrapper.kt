package xy.xy.base.widget.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import xy.xy.base.utils.Logger
import xy.xy.base.utils.exp.getSpace
import xy.xy.base.widget.recycler.holder.BaseViewHolder
import kotlin.math.abs

abstract class RecyclerAdapterWrapper<T> : RecyclerView.Adapter<BaseViewHolder>() {
    //空数据的时候
    private val emptyType = -Int.MAX_VALUE
    //头头
    val heardData by lazy { ArrayList<Int>() }
    //脚脚
    val footData by lazy { ArrayList<Int>() }
    
    fun getHeadSize() = heardData.size
    
    fun getFootSize() = footData.size

    var emptyRes :Int?=null
        set(value) {
            if (field == value)return
            field = value
            if (onItemContentCount() == 0) {
                notifyDataSetChanged()
            }
        }


    /**
     * 添加头部
     */
    fun addHeadView(resId: Int){
        heardData.add(resId)
        notifyItemInserted(getHeadSize())
    }

    fun removeHeadView(resId: Int){
        for ((index, value) in heardData.withIndex()) {
            if (value == resId){
                heardData.remove(resId)
                notifyItemRemoved(index)
                return
            }
        }
        Logger.e("headView not find")
    }

    /**
     * 添加脚脚
     */
    fun addFootView(resId: Int){
        footData.add(resId)
        notifyItemChanged(itemCount)
    }


    fun removeFootView(resId: Int){
        synchronized(this){
            for ((index, value) in footData.withIndex()) {
                if (value == resId){
                    heardData.remove(resId)
                    notifyItemRemoved(index+getHeadSize()+onItemContentCount())
                    return
                }
            }
        }
        Logger.e("footView not find")
    }

    private fun isHeadOrFootOrEmpty(position: Int) =  position < getHeadSize() || (position >= getHeadSize()+onItemContentCount())


    private fun showEmpty():Boolean{
        return emptyRes != null && onItemContentCount()<=0
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val context = parent.context
        if (viewType < 0){
            if (viewType == emptyType){
                val emptyRes = emptyRes ?: return BaseViewHolder(context.getSpace())
                return BaseViewHolder(LayoutInflater.from(context).inflate(emptyRes,parent,false))
            }
            var position = abs(viewType) - 1
            if (position in  0 until getHeadSize()){
                return BaseViewHolder(LayoutInflater.from(context).inflate(heardData[position],parent,false))
            }
            val footPosition = position - getHeadSize()
            if (footPosition in  0 until getFootSize()){
                return BaseViewHolder(LayoutInflater.from(context).inflate(footData[footPosition],parent,false))
            }
            return BaseViewHolder(context.getSpace())
        }
        return onItemCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (position < getHeadSize()){
            onItemBindHeadViewHolder(holder,heardData[position],position)
        }else if (position == getHeadSize() && showEmpty()){
            onItemBindEmptyViewHolder(holder,position)
        }else if (position >= getHeadSize() && !showEmpty() && position < (getHeadSize() + onItemContentCount())){
            onItemBindViewHolder(holder,position)
        }else {
            val footPosition = position - getHeadSize() - onItemShowContentCount()
            if (footPosition < footData.size){
                onItemBindFootViewHolder(holder,footData[footPosition],position)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        if (position < getHeadSize()){
            return -position-1
        }else if (showEmpty() && position == getHeadSize()){
            return emptyType
        }else if (position < (getHeadSize() + onItemContentCount())){
            return  onItemViewType(position-getHeadSize())
        }
        val contentCount = if (showEmpty()) 1 else onItemContentCount()
        return contentCount - position -1
    }

    override fun getItemCount(): Int = getHeadSize()+getFootSize() + onItemShowContentCount()


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
    
    
    private fun onItemShowContentCount():Int = if (showEmpty()) 1 else onItemContentCount()
    abstract fun onItemContentCount():Int
    abstract fun onItemBindViewHolder(holder: BaseViewHolder, position: Int)
    open fun onItemBindHeadViewHolder(holder: BaseViewHolder, res: Int,position:Int){}
    open fun onItemBindEmptyViewHolder(holder: BaseViewHolder,position:Int){}
    open fun onItemBindFootViewHolder(holder: BaseViewHolder, res: Int,position:Int){}
    abstract fun onItemViewType(position: Int):Int
    abstract fun onItemCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
}