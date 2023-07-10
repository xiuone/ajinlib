package com.xy.amap

import android.Manifest
import androidx.lifecycle.LifecycleOwner
import com.amap.api.location.AMapLocationListener
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.xy.amap.location.LocationConfig
import xy.xy.base.assembly.base.BaseAssemblyWithContext
import xy.xy.base.assembly.base.BaseAssemblyViewWithContext
import xy.xy.base.permission.IPermissionInterceptorCreateListener


class LocationAssembly(view: LocationAssemblyView) :BaseAssemblyWithContext<LocationAssembly.LocationAssemblyView>(view){
    private val aMapLocationClient by lazy { LocationConfig.mLocationClient }
    private val interceptor by lazy { this.view?.onCreateIPermissionInterceptor() }

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
        val act = getCurrentAct()
        val interceptor = interceptor
        if (act != null && interceptor != null){
            XXPermissions.with(act)
                .permission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .interceptor(interceptor)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: List<String?>, allGranted: Boolean) {
                        val lastLocation = LocationConfig.lastLocation
                        if (lastLocation != null && showOld){
                            this@LocationAssembly.view?.onLocationChanged(lastLocation)
                        }
                        aMapLocationClient?.startLocation()
                    }

                    override fun onDenied(permissions: List<String?>, doNotAskAgain: Boolean) {
                        super.onDenied(permissions, doNotAskAgain)
                        this@LocationAssembly.view?.onLocationPermissionError()
                    }
                })
        }
//        getContext()?.requestPermission(reasonDialog,deniedDialog,object :PermissionCallBack{
//            override fun onGranted() {
//                val lastLocation = LocationConfig.lastLocation
//                if (lastLocation != null && showOld){
//                    this@LocationAssembly.view?.onLocationChanged(lastLocation)
//                }
//                aMapLocationClient?.startLocation()
//            }
//
//            override fun onDenied() {
//                super.onDenied()
//                this@LocationAssembly.view?.onLocationPermissionError()
//            }
//        },*permissions)
    }


    interface LocationAssemblyView :BaseAssemblyViewWithContext,AMapLocationListener,IPermissionInterceptorCreateListener{
        fun onCanLocation() = true
        fun onLocationPermissionError(){}
    }
}