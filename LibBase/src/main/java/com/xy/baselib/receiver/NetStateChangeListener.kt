package com.xy.baselib.receiver

interface NetStateChangeListener {
    fun onNetStateChange(netType: NetType);
}