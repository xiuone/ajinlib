package com.xy.amap

import com.amap.api.maps.AMap
import com.amap.api.maps.model.CameraPosition
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.*


class MapMoveAssembly(view: MapMoveAssemblyView) :MapBaseAssembly<MapMoveAssembly.MapMoveAssemblyView>(view) ,
    AMap.OnCameraChangeListener,GeocodeSearch.OnGeocodeSearchListener{
    private var query :RegeocodeQuery?=null
    private val geocoderSearch by lazy { GeocodeSearch(getContext()) }
    private var latLng :LatLonPoint?=null
    private var address :RegeocodeAddress?=null

    override fun onCreateInit() {
        super.onCreateInit()
        aMap?.setOnCameraChangeListener(this)
        geocoderSearch.setOnGeocodeSearchListener(this)
    }

    override fun onCameraChange(p0: CameraPosition?) {}

    override fun onCameraChangeFinish(cameraPosition: CameraPosition?) {
        cameraPosition?.run {
            val latLng = LatLonPoint(target.latitude,target.longitude)
            this@MapMoveAssembly.latLng = latLng
            query = RegeocodeQuery(latLng, 200f, GeocodeSearch.AMAP)
            geocoderSearch.getFromLocationAsyn(query)
        }
    }


    override fun onRegeocodeSearched(result: RegeocodeResult?, resultCode: Int) {
        address = result?.regeocodeAddress
        if (result?.regeocodeQuery == query && resultCode == AMapException.CODE_AMAP_SUCCESS){
            this.view?.onMapMoveCallBack(latLng?:LatLonPoint(0.0,0.0),address)
        }
    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {}

    fun getCurrentLatLng() = latLng
    fun getCurrentAddress() = address

    interface MapMoveAssemblyView:MapBaseAssemblyView{
        fun onMapMoveCallBack(latLonPoint: LatLonPoint,address: RegeocodeAddress?)
    }

}