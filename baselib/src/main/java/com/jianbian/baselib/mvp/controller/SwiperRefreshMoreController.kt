package com.jianbian.baselib.mvp.controller

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jianbian.baselib.adapter.BaseRecyclerAdapter
import com.jianbian.baselib.mvp.impl.OnChildItemClickListener
import com.jianbian.baselib.mvp.impl.OnItemClickListener
import com.jianbian.baselib.mvp.impl.RefreshMoreImpl

class SwiperRefreshMoreController <T> : SwipeRefreshLayout.OnRefreshListener,OnItemClickListener, OnChildItemClickListener {
    private var pullTo:SwipeRefreshLayout?=null
    private var listener:RefreshMoreImpl?=null
    private var adapter:BaseRecyclerAdapter<T>?=null
    private var emtryView:FrameLayout?=null
    constructor(pullTo:SwipeRefreshLayout,recyclerView: RecyclerView,adapter:BaseRecyclerAdapter<T>,listener:RefreshMoreImpl){
        pullTo.setOnRefreshListener(this)
        this.adapter = adapter
        this.listener = listener
        this.pullTo = pullTo
        recyclerView.adapter = adapter
        adapter.onItemClickListener = this
    }

    fun getAdapter():BaseRecyclerAdapter<T>? = adapter

    fun getEntryView():View? = emtryView

    /**
     * 设置空数据加载加框
     */
    fun setEntryView(emtryView:FrameLayout,@LayoutRes emtryLayoutId:Int){
        this.emtryView = emtryView
        emtryView.removeAllViews()
        this.emtryView?.addView(LayoutInflater.from(emtryView.context).inflate(emtryLayoutId,null))
    }

    /**
     * 添加可以点击的item
     */
    fun addChildListener(@IdRes vararg viewIds: Int ){
        adapter?.addChildClickViewIds(this,*viewIds)
    }

    /**
     * 自动刷新
     */
    fun autoRefresh(){
        pullTo?.isRefreshing = true
        listener?.getData(0,0)
    }

    /**
     * 设置数据
     */
    fun setData(data:MutableList<T>?){
        pullTo?.isRefreshing = false
        adapter?.setNewData(data)
        showEmtryView()
    }

    fun addItem(item:T?){
        adapter?.addData(item)
        showEmtryView()
    }

    fun addItem(postion:Int,item:T?){
        adapter?.addData(postion,item)
        showEmtryView()
    }

    fun remove(position:Int){
        adapter?.remove(position)
        showEmtryView()
    }

    fun showEmtryView(){
        if (adapter == null || adapter!!.data.size <= 0){
            emtryView?.visibility = View.VISIBLE
        }else{
            emtryView?.visibility = View.GONE
        }
    }

    override fun onItemClick(adapter: BaseRecyclerAdapter<*>, view: View, position: Int) {
        listener?.onItemClick(adapter,view,position)
    }

    override fun onItemChildClick(adapter: BaseRecyclerAdapter<*>, view: View, position: Int) {
        listener?.onItemChildClick(adapter,view,position)
    }


    override fun onRefresh() {
        listener?.getData(0,0)
    }
}