package com.xy.amap

import com.amap.api.maps.MapView
import xy.xy.base.assembly.base.BaseAssemblyWithContext
import xy.xy.base.assembly.base.BaseAssemblyViewWithContext

abstract class MapBaseAssembly<T:MapBaseAssembly.MapBaseAssemblyView>(view:T): BaseAssemblyWithContext<T>(view) {
    protected val mapView by lazy { this.view?.onCreateMapView() }
    protected val aMap by lazy { this.mapView?.map }
    interface MapBaseAssemblyView :BaseAssemblyViewWithContext{
        fun onCreateMapView(): MapView?
    }
}