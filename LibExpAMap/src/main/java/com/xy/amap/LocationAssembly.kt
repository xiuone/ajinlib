package com.xy.amap

import androidx.lifecycle.LifecycleOwner
import com.amap.api.location.AMapLocationListener
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.xy.amap.location.LocationConfig
import com.xy.base.assembly.base.BaseAssembly
import com.xy.base.assembly.base.BaseAssemblyView
import com.xy.base.utils.permission.PermissionCallBack
import com.xy.base.utils.permission.PermissionRequestMode
import com.xy.base.utils.permission.PermissionUiListener
import com.xy.base.utils.permission.requestPermission


class LocationAssembly(view: LocationAssemblyView) :BaseAssembly<LocationAssembly.LocationAssemblyView>(view){
    private val aMapLocationClient by lazy { LocationConfig.mLocationClient }

    private val deniedDialog  by lazy { this.view?.onCreatePermissionDenied() }
    private val reasonDialog  by lazy { this.view?.onCreatePermissionReason() }
    private val permissions by lazy { this.view?.onCreatePermissionRequestMode()?: arrayOf() }

    override fun onResume(owner: LifecycleOwner?) {
        super.onResume(owner)
        if (this.view?.onCanLocation() == true) {
            getContext()?.run {
                val isPermission = XXPermissions.isGranted(this,
                    Permission.ACCESS_FINE_LOCATION,
                    Permission.ACCESS_COARSE_LOCATION)
                if (isPermission) {
                    aMapLocationClient?.startLocation()
                }
            }
        }
    }


    fun startLocation(showOld:Boolean = false){
        getContext()?.requestPermission(reasonDialog,deniedDialog,object :PermissionCallBack{
            override fun onGranted() {
                val lastLocation = LocationConfig.lastLocation
                if (lastLocation != null && showOld){
                    this@LocationAssembly.view?.onLocationChanged(lastLocation)
                }
                aMapLocationClient?.startLocation()
            }

            override fun onDenied() {
                super.onDenied()
                this@LocationAssembly.view?.onLocationPermissionError()
            }
        },*permissions)
    }


    interface LocationAssemblyView :BaseAssemblyView,PermissionUiListener,AMapLocationListener{
        fun onCanLocation() = true
        fun onLocationPermissionError(){}
        fun onCreatePermissionRequestMode():Array<PermissionRequestMode>
    }
}