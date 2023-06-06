package com.xy.base.utils.volume

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class VolumeReceiver : BroadcastReceiver() {
    private val TAG = VolumeReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.media.VOLUME_CHANGED_ACTION") {
            Log.e(TAG, "android.media.VOLUME_CHANGED_ACTION")
            VolumeManger.instance.volumeChanged()
        }
    }

}