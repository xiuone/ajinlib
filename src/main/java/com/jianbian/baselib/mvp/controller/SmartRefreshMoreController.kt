package com.jianbian.baselib.mvp.controller

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.jianbian.baselib.adapter.BaseRecyclerAdapter
import com.jianbian.baselib.mvp.impl.OnChildItemClickListener
import com.jianbian.baselib.mvp.impl.OnItemClickListener
import com.jianbian.baselib.mvp.impl.RefreshMoreImpl
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

class SmartRefreshMoreController <T> : OnRefreshListener, OnLoadMoreListener,OnItemClickListener, OnChildItemClickListener {
    private var pullTo:SmartRefreshLayout?=null
    private var defindPage = 1
    private var page = defindPage
    private var pageSize = 20
    private var listener:RefreshMoreImpl?=null
    private var adapter:BaseRecyclerAdapter<T>?=null
    constructor(pullTo:SmartRefreshLayout,recyclerView: RecyclerView,adapter:BaseRecyclerAdapter<T>,listener:RefreshMoreImpl){
        pullTo.setOnRefreshListener(this)
        pullTo?.setEnableLoadMore(true)
        pullTo?.setOnLoadMoreListener(this)
        this.adapter = adapter
        this.listener = listener
        this.pullTo = pullTo
        adapter.onItemClickListener
        recyclerView.adapter = adapter
        adapter.onItemClickListener = this
    }

    fun getDefindPage():Int = defindPage

    fun getAdapter():BaseRecyclerAdapter<T>? = adapter


    fun initPage(defindPage:Int,pageSize:Int){
        this.defindPage = defindPage
        this.pageSize = pageSize
        page = defindPage
        autoRefresh()
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
        pullTo?.autoRefresh()
    }

    /**
     * 设置数据
     */
    fun setData(data:MutableList<T>?){
        pullTo?.finishLoadMore()
        pullTo?.finishRefresh()
        if (page == defindPage)
            adapter?.setNewData(data)
        else
            adapter?.addData(data)
        if (data == null || data.size < pageSize){
            pullTo?.setNoMoreData(true)
        }else{
            pullTo?.setNoMoreData(false)
            page++
        }
    }

    override fun onItemClick(adapter: BaseRecyclerAdapter<*>, view: View, position: Int) {
        listener?.onItemClick(adapter,view,position)
    }

    override fun onItemChildClick(adapter: BaseRecyclerAdapter<*>, view: View, position: Int) {
        listener?.onItemChildClick(adapter,view,position)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        page = defindPage
        listener?.getData(page,pageSize)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        listener?.getData(page,pageSize)
    }
}