package com.xy.base.utils.volume

import android.content.IntentFilter
import com.xy.base.utils.ContextHolder
import com.xy.base.utils.notify.NotifyBase

class VolumeManger :NotifyBase<VolumeCallback>() {
    private val mVolumeReceiver :VolumeReceiver by lazy { VolumeReceiver() }
    private var isRegister = false
    fun registerVolumeReceiver() :VolumeManger{
        if (!isRegister) {
            val filter = IntentFilter()
            filter.addAction("android.media.VOLUME_CHANGED_ACTION")
            ContextHolder.getContext()?.registerReceiver(mVolumeReceiver, filter)
        }
        isRegister = true
        return this
    }

    fun unRegisterVolumeReceiver() :VolumeManger{
        if (isRegister) {
            ContextHolder.getContext()?.unregisterReceiver(mVolumeReceiver)
        }
        isRegister = false
        return this
    }


    fun volumeChanged(){
        findItem {
            it.volumeChanged()
        }
    }

    companion object{
        val instance by lazy { VolumeManger() }
    }

}