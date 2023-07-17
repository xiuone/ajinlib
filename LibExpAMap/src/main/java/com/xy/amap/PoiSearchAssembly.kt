package com.xy.amap

import android.text.TextUtils
import com.amap.api.maps.model.LatLng
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import xy.xy.base.utils.Logger


class PoiSearchAssembly(view: PoiSearchAssemblyView): MapBaseAssembly<PoiSearchAssembly.PoiSearchAssemblyView>(view),
    PoiSearch.OnPoiSearchListener {
    private var poiSearch:PoiSearch?= null
    private var query : PoiSearch.Query? = null

    fun searchKeyWord(keyWord:String?,currentPage:Int,pageSize:Int,latLng: LatLng,city:String?){

        poiSearch?.setOnPoiSearchListener(null)

        query = PoiSearch.Query(keyWord, "", city)
        if (TextUtils.isEmpty(keyWord)){
            query?.location = LatLonPoint(latLng.latitude,latLng.longitude)
            query?.cityLimit = true
            query?.isDistanceSort = true
        }
        query?.pageSize = pageSize
        query?.pageNum = currentPage
        poiSearch= PoiSearch(getContext(), query)
        poiSearch?.bound = PoiSearch.SearchBound(LatLonPoint(latLng.latitude, latLng.longitude), 50000)
        poiSearch?.setOnPoiSearchListener(this)//设置数据返回的监听器 (5)
        poiSearch?.searchPOIAsyn()//开始搜索
    }

    override fun onPoiSearched(result: PoiResult?, resultCode: Int) {
        val query = query
        Logger.d(result?.toString()?:"")
        if (query != null && result?.query == query && resultCode == AMapException.CODE_AMAP_SUCCESS){
            this.view?.onPoiSearched(result, resultCode)
        }
    }

    override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {}

    interface PoiSearchAssemblyView :MapBaseAssemblyView {
        fun onPoiSearched(result: PoiResult?, resultCode: Int)
    }
}