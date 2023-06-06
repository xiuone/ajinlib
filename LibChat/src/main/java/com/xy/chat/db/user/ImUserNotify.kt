package com.xy.chat.db.user

import com.xy.base.utils.notify.NotifyBase

abstract class ImUserNotify :NotifyBase<ImUserNotify.ImUserChangeListener>() {
    protected fun onImUserChangeCallBack(userBean: ImUser){
        findItem {
            it.onImUserChangeCallBack(userBean)
        }
    }

    interface ImUserChangeListener {
        fun onImUserChangeCallBack(imUser: ImUser)
    }
}