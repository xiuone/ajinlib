package com.xy.baselib.mvp.controller

import androidx.recyclerview.widget.RecyclerView
import com.xy.baselib.mvp.impl.RefreshMoreImpl
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.xiuone.adapter.adapter.RecyclerBaseAdapter

class SmartRefreshMoreController <T> : OnRefreshListener, OnLoadMoreListener{
    private var pullTo:SmartRefreshLayout?=null
    private var defindPage = 1
    private var page = defindPage
    private var pageSize = 20
    private var listener:RefreshMoreImpl?=null
    private var adapter:RecyclerBaseAdapter<T>?=null
    constructor(pullTo:SmartRefreshLayout,recyclerView: RecyclerView,adapter:RecyclerBaseAdapter<T>,listener:RefreshMoreImpl){
        pullTo.setOnRefreshListener(this)
        pullTo?.setEnableLoadMore(true)
        pullTo?.setOnLoadMoreListener(this)
        this.adapter = adapter
        this.listener = listener
        this.pullTo = pullTo
        recyclerView.adapter = adapter
    }

    fun getDefindPage():Int = defindPage

    fun getAdapter():RecyclerBaseAdapter<T>? = adapter


    fun initPage(defindPage:Int,pageSize:Int){
        this.defindPage = defindPage
        this.pageSize = pageSize
        page = defindPage
        autoRefresh()
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
            adapter?.dataController?.setNewData(data)
        else
            adapter?.dataController?.addData(data)
        if (data == null || data.size < pageSize){
            pullTo?.setNoMoreData(true)
        }else{
            pullTo?.setNoMoreData(false)
            page++
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        page = defindPage
        listener?.getData(page,pageSize)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        listener?.getData(page,pageSize)
    }
}