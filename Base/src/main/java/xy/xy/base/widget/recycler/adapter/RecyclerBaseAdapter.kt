package xy.xy.base.widget.recycler.adapter

import android.annotation.SuppressLint
import android.view.View
import xy.xy.base.utils.exp.setOnClick
import xy.xy.base.widget.recycler.holder.BaseViewHolder
import xy.xy.base.widget.recycler.listener.*
import kotlin.math.max
import kotlin.math.min

abstract class RecyclerBaseAdapter<T> : RecyclerAdapterWrapper<BaseViewHolder>(){
    protected val VIEW_TYPE_SPACE = 1
    protected val VIEW_TYPE_CONTENT = 2
    private val implList by lazy { ArrayList<ItemHeadFootListener>() }

    val data :ArrayList<T> by lazy { ArrayList() }


    var itemClickListener: OnItemClickListener<T>?=null
    var itemLongListener: OnItemLongClickListener<T>?=null
    var itemTouchListener: OnItemTouchListener<T>?= null

    private val childClickMap :HashMap<Int, OnChildItemClickListener<T>> by lazy {  HashMap() }
    private val childLongClickMap :HashMap<Int, OnChildItemLongClickListener<T>> by lazy {  HashMap() }

    private val childTouchMap :HashMap<Int, OnItemTouchListener<T>> by lazy {  HashMap() }

    fun addChildClicked(listener: OnChildItemClickListener<T>?, vararg viewIds: Int?): RecyclerBaseAdapter<T> {
        synchronized(this){
            for (item in viewIds){
                if (item != null)
                    addChildClicked(item,listener)
            }
            return this
        }
    }

    fun addChildClicked(viewIds: Int?,listener: OnChildItemClickListener<T>?): RecyclerBaseAdapter<T> {
        synchronized(this){
            if (listener == null || viewIds == null)return this
            childClickMap[viewIds] = listener
            return this
        }
    }

    fun addChildLongClicked(listener: OnChildItemLongClickListener<T>?, vararg viewIds: Int?): RecyclerBaseAdapter<T> {
        synchronized(this){
            for (item in viewIds){
                if (item != null)
                    addChildLongClicked(item,listener)
            }
            return this
        }
    }

    fun addChildLongClicked(viewIds: Int?,listener: OnChildItemLongClickListener<T>?): RecyclerBaseAdapter<T> {
        synchronized(this){
            if (listener == null || viewIds == null)return this
            childLongClickMap[viewIds] = listener
            return this
        }
    }

    fun addChildTouch(viewIds: Int,listener: OnItemTouchListener<T>?): RecyclerBaseAdapter<T> {
        synchronized(this){
            if (listener == null)return this
            childTouchMap[viewIds] = listener
            return this
        }
    }


    open fun setNewData(data: MutableList<T>?){
        synchronized(this){
            val oldAll = this.data.size
            this.data.clear()
            data?.run {
                this@RecyclerBaseAdapter.data.addAll(this)
            }

            notifyItemRangeChanged(getHeadSize(), max(oldAll,this.data.size))
        }
    }

    fun addData(data: MutableList<T>?){
        synchronized(this){
            if (this.data.size<=0) {
                setNewData(data)
            }else if (data != null){
                val start: Int = this.data.size+getHeadSize()
                this@RecyclerBaseAdapter.data.addAll(data)
                notifyItemRangeChanged(start, this.data.size+getHeadSize())
            }
        }
    }

    fun addData(data: MutableList<T>?,position: Int){
        synchronized(this){
            if (data == null)return
            if (this.data.size<=0) {
                setNewData(data)
            }else if (position >= data.size){
                val start: Int = this.data.size+getHeadSize()
                this@RecyclerBaseAdapter.data.addAll(data)
                notifyItemRangeChanged(start, this.data.size+getHeadSize())
            }else{
                this@RecyclerBaseAdapter.data.addAll(position,data)
                notifyItemRangeChanged(getHeadSize()+position, getHeadSize()+this.data.size)
            }
        }
    }



    fun addItem(data: T){
        val list = ArrayList<T>()
        list.add(data)
        addData(list)
    }

    fun addItem(data: T,position: Int){
        synchronized(this){
            if (position>= this.data.size){
                addItem(data)
            }else{
                this@RecyclerBaseAdapter.data.add(position,data)
                notifyItemRangeChanged(getHeadSize()+position, getHeadSize()+this.data.size)
            }
        }

    }

    fun removeItem(position: Int?):T?{
        synchronized(this){
            if (position == null)return null
            var item: T? = null
            if (position < this.data.size) {
                item = data.removeAt(position)
                notifyItemRemoved(position+getHeadSize())
                notifyItemRangeChanged(getHeadSize()+position, getHeadSize()+this.data.size - position)
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
                val last = min(index + count,data.size) - 1
                for (i in last downTo index) {
                    data.removeAt(i)
                }
                notifyItemRangeRemoved(getHeadSize() + index, count)
                notifyItemRangeChanged(getHeadSize() + index, getHeadSize() + data.size - index)
                if (this.data.size<0){
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun notifyItemChangedWithHead(index: Int) = notifyItemChanged(index+getHeadSize())


    override fun onItemContentCount(): Int = data.size

    @SuppressLint("ClickableViewAccessibility")
    override fun onItemBindViewHolder(holder: BaseViewHolder, position: Int) {
        val newPosition = position - getHeadSize()
        if (newPosition< data.size) {
            val data = data[newPosition]
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


    abstract fun onBindViewHolder(holder: BaseViewHolder, data:T, position: Int)




    fun bindHeadFootImpl(listener:ItemHeadFootListener){
        synchronized(this){
            implList.add(listener)
            listener.addHeadOrFoot(this)
        }
    }

    override fun onItemBindHeadViewHolder(holder: BaseViewHolder, res: Int, position: Int) {
        super.onItemBindHeadViewHolder(holder, res, position)
        synchronized(this){
            for (listener in implList){
                if (listener.onItemBindHeadViewHolder(holder,res, position))
                    return@synchronized
            }
        }
    }

    override fun onItemBindFootViewHolder(holder: BaseViewHolder, res: Int, position: Int) {
        super.onItemBindFootViewHolder(holder, res, position)
        synchronized(this){
            for (listener in implList){
                if (listener.onItemBindHeadViewHolder(holder,res, position))
                    return@synchronized
            }
        }
    }

    interface ItemHeadFootListener{
        fun onItemBindHeadViewHolder(holder: BaseViewHolder, res: Int, position: Int):Boolean
        fun addHeadOrFoot(adapter:RecyclerBaseAdapter<*>)
    }
}