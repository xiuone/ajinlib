package com.xy.baselib.widget.refresh

import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.xy.baselib.widget.recycler.adapter.RecyclerBaseAdapter

open class RefreshRecyclerController<T>(private val refreshLayout:SmartRefreshLayout,
                                        private val adapter:RecyclerBaseAdapter<T>,
                                        private val refreshListener:RefreshListener,private val startPage:Int = 0) :
    OnRefreshListener, OnRefreshLoadMoreListener {
    protected var page = startPage
    protected var noMoreData = true

    fun init():RefreshRecyclerController<T>{
        return init(true)
    }

    fun init(refresh:Boolean):RefreshRecyclerController<T>{
        page = startPage
        noMoreData = true
        refreshLayout.setEnableLoadMore(true)
        refreshLayout.autoRefresh()
        refreshLayout.setOnRefreshLoadMoreListener(this)
        refreshLayout.setOnRefreshListener(this)
        refreshLayout.setNoMoreData(false)
        return autoRefresh(refresh)
    }

    fun autoRefresh(status:Boolean):RefreshRecyclerController<T>{
        if (status)
            refreshLayout.autoRefresh()
        return this
    }

    fun setEnableLoadMore(enableLoadMore:Boolean){
        refreshLayout.setEnableLoadMore(enableLoadMore)
    }

    protected fun resetNoMoreData(){
        noMoreData = false;
    }

    fun setOnlyData(data: MutableList<T>?,nextPage:Int = page+1 ,noMoreData:Boolean = true){
        if (page == startPage){
            setNewData(data, nextPage, noMoreData)
        }else{
            addData(data, nextPage, noMoreData)
        }
    }

    fun setNewData(data: MutableList<T>?,nextPage:Int = page+1 ,noMoreData:Boolean = true){
        val newData = ArrayList(data)
        adapter.setNewData(newData)
        refreshLayout.finishRefresh(500,true,noMoreData)
        finishLoadMore(newData,nextPage,noMoreData)
    }

    fun addData(data: MutableList<T>?,nextPage:Int = page+1,noMoreData:Boolean = true){
        if (page == startPage){
            setNewData(data,nextPage,noMoreData)
        }else {
            adapter.addData(data)
            finishLoadMore(data,nextPage,noMoreData)
        }
    }


    fun finishLoadMore(data: MutableList<T>?,nextPage:Int,noMoreData:Boolean = true){
        page = nextPage
        refreshLayout.resetNoMoreData()
        resetNoMoreData()
        val newNoMoreData = noMoreData || data == null || data.isEmpty()
        refreshLayout.finishLoadMore(500,true,newNoMoreData)
        this.noMoreData = newNoMoreData
    }

    fun loadError(){
        refreshLayout.finishLoadMore(500,false,this.noMoreData)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        page = startPage
        refreshListener.onRefresh(page)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        refreshListener.onRefresh(page)
    }
}