package com.xy.baselib.notify.config

import com.xy.baselib.notify.NotifyBase

class ConfigNotify : NotifyBase<ConfigListener>(){

    fun switchRes(){
        findItem {
            it.onChangeConfig()
        }
    }
}