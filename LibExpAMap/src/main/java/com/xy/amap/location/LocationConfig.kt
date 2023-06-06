package com.xy.amap.location

import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.xy.base.utils.ContextHolder

object LocationConfig {

    val mLocationClient by lazy { getLocation() }

    var lastLocation: AMapLocation?=null

    /**
     * 定位初始化
     * */
    private fun getLocation(): AMapLocationClient?{

        val mLocationClient = AMapLocationClient(ContextHolder.getContext())
        val mLocationOption = AMapLocationClientOption()
        mLocationOption.isNeedAddress = true
        //设置为高精度定位模式
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        //该方法默认为false。
        mLocationOption.isOnceLocation = true
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.isOnceLocationLatest = true
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption)
        mLocationClient.setLocationListener(LocationNotify.instance)
        //设置是否返回地址信息（默认返回地址信息）
        return mLocationClient
    }
}