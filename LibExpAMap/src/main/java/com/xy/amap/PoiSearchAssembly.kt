package com.xy.amap

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.maps2d.model.LatLng
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.xy.base.widget.recycler.adapter.RecyclerBaseAdapter


class PoiSearchAssembly(view: PoiSearchAssemblyView): MapBaseAssembly<PoiSearchAssembly.PoiSearchAssemblyView>(view),
    PoiSearch.OnPoiSearchListener {

    private val recyclerView by lazy { this.view?.onCreateRecyclerView() }
    private val adapter by lazy { this.view?.onCreateAdapter() }
    private var poiSearch:PoiSearch?= null
    private var query : PoiSearch.Query? = null

    override fun onCreateInit() {
        super.onCreateInit()
        recyclerView?.layoutManager = LinearLayoutManager(getContext())
        recyclerView?.adapter = adapter
    }


    fun searchKeyWord(keyWord:String?,currentPage:Int,pageSize:Int,latLng: LatLng){
        query = if (keyWord.isNullOrEmpty()) {
            PoiSearch.Query("", "住宿服务|公司企业", null)
        }else {
            PoiSearch.Query(keyWord, "住宿服务|公司企业", null)
        }
        query?.pageSize = pageSize // 设置每页最多返回多少条poiitem
        query?.pageNum = currentPage //设置查询页码

        poiSearch?.setOnPoiSearchListener(null)
        query?.location = LatLonPoint(latLng.latitude,latLng.longitude)
        poiSearch = PoiSearch(getContext(), query);
        poiSearch?.setOnPoiSearchListener(this);//设置数据返回的监听器 (5)
        poiSearch?.searchPOIAsyn();//开始搜索
    }

    override fun onPoiSearched(result: PoiResult?, resultCode: Int) {
        val query = query
        if (query != null && result?.query == query && resultCode == 0){
            val poiItems = result.pois ?:ArrayList()
            if (query.pageNum == 0){
                adapter?.setNewData(poiItems)
            }else{
                adapter?.addData(poiItems)
            }
            this.view?.onPoiSearched(query.pageNum,poiItems.size)
        }
        checkEmpty()
    }

    fun checkEmpty(){
        if (adapter?.data.isNullOrEmpty()){
            adapter?.emptyView = this.view?.onCreateEmptyView()
        }
    }

    override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {}

    interface PoiSearchAssemblyView :MapBaseAssemblyView {
        fun onCreateRecyclerView(): RecyclerView?
        fun onCreateAdapter(): RecyclerBaseAdapter<PoiItem>?
        fun onCreateEmptyView():View?
        fun onPoiSearched(page:Int,size:Int)
    }
}