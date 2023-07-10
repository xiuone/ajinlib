package com.xy.amap.location

import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationListener
import xy.xy.base.utils.notify.NotifyBase


class LocationNotify :NotifyBase<AMapLocationListener>() ,AMapLocationListener{

    override fun onLocationChanged(p0: AMapLocation?) = findItem { it.onLocationChanged(p0) }

    companion object{
        val instance by lazy { LocationNotify() }
    }
}