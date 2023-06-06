package com.xy.amap

import com.amap.api.maps2d.MapView
import com.xy.base.assembly.base.BaseAssembly
import com.xy.base.assembly.base.BaseAssemblyView

abstract class MapBaseAssembly<T:MapBaseAssembly.MapBaseAssemblyView>(view:T): BaseAssembly<T>(view) {
    protected val mapView by lazy { this.view?.onCreateMapView() }
    protected val aMap by lazy { this.mapView?.map }
    interface MapBaseAssemblyView :BaseAssemblyView{
        fun onCreateMapView(): MapView?
    }
}