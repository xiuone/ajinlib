package com.xy.amap

import android.graphics.Bitmap
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.model.BitmapDescriptorFactory
import com.amap.api.maps2d.model.LatLng
import com.amap.api.maps2d.model.Marker
import com.amap.api.maps2d.model.MarkerOptions
import com.xy.base.assembly.base.BaseAssemblyWithContext
import com.xy.base.assembly.base.BaseAssemblyViewWithContext

class MarkAssembly(view: MarkAssemblyView):BaseAssemblyWithContext<MarkAssembly.MarkAssemblyView>(view), AMap.OnMarkerClickListener {
    private val mapView by lazy { this.view?.onCreateMapView() }
    private val aMap by lazy { mapView?.map }
    private val markerHashMap by lazy { HashMap<String, MarkTagMode>() }

    override fun onCreateInit() {
        super.onCreateInit()
        aMap?.setOnMarkerClickListener(this)
    }

    /**
     * 显示bitmap
     */
    fun showMarker(tag: String, lat: Double, lng: Double, bitmap: Bitmap, any: Any? = null): Marker? =
        showMarker(tag, LatLng(lat, lng), bitmap, any)

    /**
     * 显示bitmap
     */
    fun showMarker(tag: String, latLng: LatLng, bitmap: Bitmap, any: Any? = null): Marker? {
        val options = MarkerOptions()
        options.position(latLng)
        options.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        val marker = aMap?.addMarker(MarkerOptions().position(latLng)
            .icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
        if (marker != null && any != null)
            markerHashMap[tag] = MarkTagMode(marker, latLng, any)
        return marker
    }

    fun removeMark(tag: String) {
        markerHashMap[tag]?.marker?.remove()
        markerHashMap.remove(tag)
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        for (entry in markerHashMap.entries) {
            if (marker == entry.value.marker) {
                val any = entry.value.any ?: return false
                return this.view?.onMarkClicked(entry.value.latlng, any) == true
            }
        }
        return false
    }

    data class MarkTagMode(val marker:Marker,val latlng:LatLng,val any: Any?)

    interface MarkAssemblyView : BaseAssemblyViewWithContext, MapBaseAssembly.MapBaseAssemblyView {
        fun onMarkClicked(latLng: LatLng, any: Any): Boolean = false
    }
}