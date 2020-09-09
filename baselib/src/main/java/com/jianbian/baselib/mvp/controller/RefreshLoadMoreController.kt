package com.jianbian.baselib.mvp.controller

import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jianbian.baselib.adapter.BaseRecyclerAdapter
import com.jianbian.baselib.mvp.impl.RecyclerViewlmpl
import com.jianbian.baselib.view.LibRecyclerView

class RefreshLoadMoreController<T> : SwipeRefreshLayout.OnRefreshListener {
    private var recyclerView : LibRecyclerView?=null
    var adapter: BaseRecyclerAdapter<T>?=null
    private var listener:RecyclerViewlmpl?=null
    private var refreshView:SwipeRefreshLayout?=null
    private var page:Int = 1
    var defindPage = 1
    var everyDataSize =10
    private var loadMoreStatus:Boolean = false

    fun bindRecyclerView(recyclerView: LibRecyclerView, layoutManager: RecyclerView.LayoutManager, adapter: BaseRecyclerAdapter<T>){
        this.recyclerView = recyclerView
        this.adapter = adapter
        recyclerView.getRecyclerView().layoutManager = layoutManager
        recyclerView.getRecyclerView().adapter = adapter
        adapter.recyclerView = recyclerView
    }

    fun bindRefresh(refresh:SwipeRefreshLayout,listener: RecyclerViewlmpl,refreshStatus:Boolean = true,loadMoreStatus:Boolean = true){
        this.refreshView = refresh
        this.listener = listener
        this.loadMoreStatus = loadMoreStatus
//        refresh.setOnRefreshListener(this)
    }

    fun setDataHaveMore(data:MutableList<T>){
        if (page == defindPage) {
            adapter?.setNewData(data)
        }else
            adapter?.addData(data)
        refreshView?.isRefreshing = false
        if (everyDataSize < 0){
            if (data.size <= 0){
//                adapter?.loadMoreModule?.loadMoreEnd()
            }
        }else if (data.size < everyDataSize){
//            adapter?.loadMoreModule?.loadMoreEnd()
        }
    }

//    override fun onLoadMore() {
//        page++
//        refreshView?.run {
//            if (isRefreshing)
//                return
//        }
//        listener?.getData(page)
//    }

    override fun onRefresh() {
        refreshView?.isRefreshing =true
//        adapter?.run {
//            if (loadMoreModule.isLoading){
//                refreshView?.isRefreshing = false
//                return
//            }
//        }
        page = defindPage
        listener?.getData(page)
    }
}