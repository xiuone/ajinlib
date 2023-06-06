package com.xy.amap

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.LocationSource
import com.amap.api.maps2d.LocationSource.OnLocationChangedListener
import com.amap.api.maps2d.model.*
import com.amap.api.maps2d.model.MyLocationStyle.LOCATION_TYPE_LOCATE
import com.xy.amap.location.LocationNotify
import com.xy.base.utils.permission.PermissionUiListener


class MapAssembly(view: MapAssemblyView) : MapBaseAssembly<MapAssembly.MapAssemblyView>(view),
    LocationSource , AMapLocationListener{

    private val locationAssembly by lazy { this.view?.onCreateLocationAssembly() }

    private var latLng:LatLng?=null

    private val zoomNumber = 12F
    private var mListener: OnLocationChangedListener? = null


    override fun onCreateInit(savedInstanceState: Bundle?) {
        super.onCreateInit(savedInstanceState)
        LocationNotify.instance.addNotify(liftTag,this)
        mapView?.onCreate(savedInstanceState)
        aMap?.moveCamera(CameraUpdateFactory.zoomTo(zoomNumber))
        aMap?.setLocationSource(this)
        aMap?.isMyLocationEnabled = true
        aMap?.setMyLocationType(LOCATION_TYPE_LOCATE)
        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        //连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。
        // （1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        val myLocationStyle = MyLocationStyle();
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.interval(2000)
        //设置定位蓝点的Style
        aMap?.setMyLocationStyle(myLocationStyle);
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap?.isMyLocationEnabled = true;
        //marker点击事件
        //连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER)
        val uiSettings = aMap?.uiSettings
        uiSettings?.isZoomControlsEnabled = true
        uiSettings?.isMyLocationButtonEnabled = false

    }

    /**
     * 激活定位
     */
    override fun activate(onLocationChangedListener: OnLocationChangedListener?) {
        mListener = onLocationChangedListener
        LocationNotify.instance.addNotify(liftTag,this)
        locationAssembly?.startLocation()
    }

    /**
     * 停止激活   相当于停止定位
     */
    override fun deactivate() {
        mListener = null;
        LocationNotify.instance.removeNotify(liftTag)
    }

    /**
     * 定位状态变化
     */
    override fun onLocationChanged(aMapLocation: AMapLocation?) {
        if (aMapLocation == null || aMapLocation.errorCode != 0)return
        val mListener = this.mListener
        if (mListener != null){
            mListener.onLocationChanged(aMapLocation)
        }else{
            showCurrentLocation(aMapLocation)
        }
        this.mListener = null
    }

    /**
     * 显示当前定位
     */
    private fun showCurrentLocation(aMapLocation: AMapLocation){
        val latLng = LatLng(aMapLocation.latitude, aMapLocation.longitude)
        this.latLng = latLng
        showCurrentLocation(latLng)
    }

    fun showCurrentLocation(latLng:LatLng){
        val zoom = 17F
        val camera = CameraUpdateFactory.newCameraPosition(CameraPosition(latLng, zoom, 0F, 0F))
        aMap?.moveCamera(camera)
        aMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))
    }



    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }


    override fun onPause(owner: LifecycleOwner?) {
        super.onPause(owner)
        mapView?.onPause()
    }

    override fun onResume(owner: LifecycleOwner?) {
        super.onResume(owner)
        mapView?.onResume()
    }

    override fun onDestroyed(owner: LifecycleOwner) {
        super.onDestroyed(owner)
        mapView?.onDestroy()
        LocationNotify.instance.removeNotify(liftTag)
    }

    interface MapAssemblyView : MapBaseAssemblyView, PermissionUiListener {
        fun onCreateLocationAssembly(): LocationAssembly?
    }

}