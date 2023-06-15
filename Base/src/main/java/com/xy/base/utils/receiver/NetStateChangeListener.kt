package com.xy.base.utils.receiver

interface NetStateChangeListener {
    fun onNetStateChange(netType: NetType)
}