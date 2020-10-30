package com.jianbian.baselib.adapter

import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import com.jianbian.baselib.mvp.impl.OnChildItemClickListener
import com.jianbian.baselib.mvp.impl.OnItemClickListener
import com.jianbian.baselib.utils.setOnClick

open class  ViewHolder : RecyclerView.ViewHolder {
    private val views: SparseArray<View?> = SparseArray()
    constructor(itemView: View,adapter:BaseRecyclerAdapter<*>
                ,itemClickListener: OnItemClickListener?=null
                ,onChildItemClickListener:OnChildItemClickListener ?= null
                ,viewIds: ArrayList<Int>) : super(itemView) {
        if (itemClickListener != null){
            itemView.setOnClick(View.OnClickListener {
                itemClickListener.onItemClick(adapter,it,layoutPosition)
            })
        }

        if (onChildItemClickListener != null){
            for (id in viewIds){
                itemView.findViewById<View>(id).setOnClick(View.OnClickListener {
                    onChildItemClickListener.onItemChildClick(adapter,it,layoutPosition)
                })
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
            views.put(viewId, view)
        }
        return view as VI?
    }

}