package com.jianbian.baselib.adapter

import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import com.jianbian.baselib.mvp.impl.OnChildItemClickListener
import com.jianbian.baselib.mvp.impl.OnChildItemLongClickListener
import com.jianbian.baselib.mvp.impl.OnItemClickListener
import com.jianbian.baselib.mvp.impl.OnItemLongClickListener
import com.jianbian.baselib.utils.setOnClick

open class  ViewHolder (itemView: View,val adapter: BaseRecyclerAdapter<*>): RecyclerView.ViewHolder(itemView) {
    private val views: SparseArray<View?> = SparseArray()
    fun setItemClickListener(itemClickListener:OnItemClickListener?){
        if (itemClickListener!=null)
            itemView.setOnClick(View.OnClickListener {
                itemClickListener.onItemClick(adapter,it,layoutPosition)

            })
    }

    fun setChildItemClickListener(viewIds: ArrayList<Int>?,onChildItemClickListener:OnChildItemClickListener?){
        if (viewIds == null || onChildItemClickListener == null)return
        for (id in viewIds){
            getView<View>(id)?.setOnClick(View.OnClickListener {
                onChildItemClickListener.onItemChildClick(adapter,it,layoutPosition)
            })
        }
    }

    fun setItemLongClickListener(itemClickListener:OnItemLongClickListener?){
        if (itemClickListener == null)return
        itemView.setOnLongClickListener { p0 ->
            itemClickListener.onItemLongClick(adapter,p0,layoutPosition)
            false
        }
    }

    fun setChildItemLongClickListener(viewIds: ArrayList<Int>?,onChildItemClickListener:OnChildItemLongClickListener?){
        if (viewIds == null || onChildItemClickListener == null)return
        for (id in viewIds){
            getView<View>(id)?.setOnLongClickListener { p0 ->
                onChildItemClickListener.onItemChildLongClick(adapter,p0,layoutPosition)
                false
            }
        }
    }


    fun setText(@IdRes viewId: Int, string: String?){
        string?.run {
            val textView = getTextView(viewId)
            textView?.text = this
        }
    }


    fun getTextView(@IdRes viewId: Int): TextView? {
        return getView<TextView>(viewId)
    }

    fun getImageVIew(@IdRes viewId: Int): ImageView? {
        return getView<ImageView>(viewId)
    }

    fun<VI : View> getView(@IdRes viewId: Int): VI? {
        var view = views[viewId]
        if (view == null) {
            view = itemView.findViewById(viewId)
        }
        if (view != null) {
            views.put(viewId, view)
            return view as VI
        }else
            return null
    }

}