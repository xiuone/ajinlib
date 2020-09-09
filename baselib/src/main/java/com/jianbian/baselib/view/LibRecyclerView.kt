package com.jianbian.baselib.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.jianbian.baselib.R
import com.jianbian.baselib.adapter.BaseRecyclerAdapter
import kotlinx.android.synthetic.main.lib_layout_recyclerview.view.*

class LibRecyclerView(context: Context,attributeSet: AttributeSet) :FrameLayout(context,attributeSet) {
    init {
        this.addView(LayoutInflater.from(context).inflate(R.layout.lib_layout_recyclerview,null))
    }

    fun getRecyclerView():RecyclerView{
        return lib_recyclerView
    }
    fun resetView(adapter: BaseRecyclerAdapter<*>){
        adapter?.run {
            if (this.data.size>0){
                lib_recyclerView.visibility = View.VISIBLE
                empty_view.visibility =View.GONE
            }else{
                lib_recyclerView.visibility =View.GONE
                empty_view.visibility = View.VISIBLE
            }
        }
    }

    fun setEmtryView(view:View){
        empty_view.removeAllViews()
        empty_view.addView(view)
    }

    fun showEmtryView(boolean: Boolean){
        if (boolean){
            empty_view.visibility = View.VISIBLE
        }else{
            empty_view.visibility = View.GONE
        }
    }
}