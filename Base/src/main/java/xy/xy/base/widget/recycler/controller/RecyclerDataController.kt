package xy.xy.base.widget.recycler.controller

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xy.xy.base.widget.recycler.adapter.RecyclerBaseAdapter

/**
 * 用于控制recyclerView的数量
 */
class RecyclerDataController<T>(adapter: RecyclerBaseAdapter<T>) : RecyclerHeadFootController<T>(adapter){
    val datas = ArrayList<T>()
    private var entryView:View?=null
    private var init = false//刚刚初始化

    fun setEntryView(entryView: View){
        this.entryView = entryView
        entryView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun getEntryView():View?= entryView

    /**
     * 获取总共有多少数据
     */
    override fun getDataSize():Int{
        if (datas.size>0)return datas.size
        if (init && entryView != null)return 1
        return  0
    }

    fun getDataSizeNotEntryView():Int = datas.size

    fun setData(data:List<T>?,isNew: Boolean){
        if (data == null)return
        if (isNew)
            this.datas.clear()
        this.datas.addAll(data)
        init = true
    }

    fun setNewData(data:List<T>?){
        if (data == null)return
        this.datas.clear()
        this.datas.addAll(data)
        init = true
        adapter.notifyDataSetChanged()
    }

    fun addData(data: List<T>?){
        if (data == null)return
        datas.addAll(data)
        if (data.size == datas.size)
            adapter.notifyDataSetChanged()
        else{
            adapter.notifyItemRangeInserted(getDataSize()+getHeadSize() - data.size, data.size)
        }
    }

    fun addData(position:Int,item: T?){
        if (item == null)return
        if (position in 0 until datas.size) {
            datas.add(position, item)
            adapter.notifyItemInserted(getHeadSize()+position)
        } else {
            addItem(item)
        }
    }

    fun addItem(item: T?){
        if (item == null)return
        datas.add(item)
        if (datas.size >1 ){
            adapter.notifyItemInserted(getDataSize()+getHeadSize())
        }else{
            adapter.notifyDataSetChanged()
        }
    }

    fun remove(position: Int){
        if (position in 0 until datas.size){
            datas.removeAt(position)
            adapter.notifyItemRemoved(position+getHeadSize())
        }
    }

    fun getItem(position: Int) : T?= if (position in 0 until datas.size) datas[position] else null
}