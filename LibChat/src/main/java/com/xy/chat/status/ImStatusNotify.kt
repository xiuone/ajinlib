package com.xy.chat.status

import  com.xy.base.utils.notify.NotifyBase
import com.xy.chat.type.ImStatusCode

class ImStatusNotify : NotifyBase<ImStatusNotify.ImStatusListener>() {

    fun onImStatusCallBack(statusCode: ImStatusCode) = findItem { it.onImStatusCallBack(statusCode) }

    interface ImStatusListener {
        fun onImStatusCallBack(statusCode: ImStatusCode)

    }

    companion object{
        val instance by lazy { ImStatusNotify() }
    }
}