package com.xy.baselib.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.xy.baselib.exp.getCurrentNetType
import com.xy.baselib.exp.isNetworkConnected
import com.xy.baselib.exp.isWifiConnected

class NetStateChangeReceiver : BroadcastReceiver() {
    private val action = ConnectivityManager.CONNECTIVITY_ACTION;
    private var netType :NetType = NetType.NOTWORK
    private var netState :NetType = NetType.NOTWORK
    private val netTypeList by lazy { ArrayList<NetTypeChangeListener>() }
    private val netStateList by lazy { ArrayList<NetStateChangeListener>() }

    private fun getCurrentNetState(context: Context):NetType = if (!context.isNetworkConnected()) NetType.NOTWORK else if (context.isWifiConnected()) NetType.WIFI else NetType.MOBILE
    override fun onReceive(context: Context, intent: Intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION == intent.action){
            val applicationContext = context.applicationContext?:context
            val netType = applicationContext.getCurrentNetType()
            val netState = getCurrentNetState(applicationContext)
            if (netType  != this.netType){
                notifyNetTypeChanged(netType)
            }
            if (netState != this.netState){
                notifyNetStateChanged(netState)
            }

            this.netType = netType
            this.netState = netState
        }
    }

    fun registerReceiver(context: Context) {
        val intentFilter = IntentFilter()
        intentFilter.addAction(action)
        val applicationContext = context.applicationContext?:context
        netType = applicationContext.getCurrentNetType()
        netState = getCurrentNetState(applicationContext)
        applicationContext.registerReceiver(this, intentFilter)
    }

    fun unRegisterReceiver(context: Context){
        val applicationContext = context.applicationContext?:context
        applicationContext.unregisterReceiver(this)
    }

    private fun addNetTypeChange(listener:NetTypeChangeListener){
        synchronized(this){
            if (netTypeList.contains(listener))return
            netTypeList.add(listener)
        }
    }

    private fun removeNetTypeChange(listener:NetTypeChangeListener){
        synchronized(this){
            netTypeList.remove(listener)
        }
    }

    private fun addNetStateChange(listener:NetStateChangeListener){
        synchronized(this){
            if (netStateList.contains(listener))return
            netStateList.add(listener)
        }
    }

    private fun removeNetTypeChange(listener:NetStateChangeListener){
        synchronized(this){
            netStateList.remove(listener)
        }
    }

    private fun notifyNetTypeChanged(netType: NetType){
        synchronized(this){
            for (listener in netTypeList){
                listener.onNetTypeChange(netType)
            }
        }
    }

    private fun notifyNetStateChanged(netType: NetType){
        synchronized(this){
            for (listener in netStateList){
                listener.onNetStateChange(netType)
            }
        }
    }
}