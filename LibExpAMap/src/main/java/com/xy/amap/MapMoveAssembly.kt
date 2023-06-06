package com.xy.amap

import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.model.CameraPosition
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.*

class MapMoveAssembly(view: MapMoveAssemblyView) :MapBaseAssembly<MapMoveAssembly.MapMoveAssemblyView>(view) ,
    AMap.OnCameraChangeListener,GeocodeSearch.OnGeocodeSearchListener{
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
            this@MapMoveAssembly.view?.onMapMoveCallBack(latLng,null)
        }
    }


    override fun onRegeocodeSearched(result: RegeocodeResult?, p1: Int) {
        val query = result?.regeocodeQuery?.point?:return
        address = result.regeocodeAddress
        latLng = query
        if (query.latitude == latLng?.latitude && query.longitude == latLng?.longitude){
            this.view?.onMapMoveCallBack(query,result.regeocodeAddress)
        }
    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {}

    fun getCurrentLatLng() = latLng
    fun getCurrentAddress() = address

    interface MapMoveAssemblyView:MapBaseAssemblyView{
        fun onMapMoveCallBack(latLonPoint: LatLonPoint,address: RegeocodeAddress?)
    }

}