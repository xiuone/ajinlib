package com.xy.chat.status

import  com.xy.base.utils.notify.NotifyBase

class ImStatusNotify : NotifyBase<ImStatusNotify.ImStatusListener>() {

    //链接登录的时候
    fun onConnected() = findItem { it.onConnected() }
    //退出登录的时候
    fun onLoginOut() = findItem { it.onLoginOut() }
    // 账户被删除的时候
    fun onUserRemove() = findItem { it.onUserRemove() }
    //账户在另外一台设备登录
    fun onUserLoginAnOtherDevice() = findItem { it.onUserLoginAnOtherDevice() }
    //被其他设备踢出
    fun onUserKickedByOtherDevice() = findItem { it.onUserKickedByOtherDevice() }


    interface ImStatusListener {
        //链接登录的时候
        fun onConnected(){}
        //退出登录的时候
        fun onLoginOut(){}
        // 账户被删除的时候
        fun onUserRemove(){}
        //账户在另外一台设备登录
        fun onUserLoginAnOtherDevice(){}
        //被其他设备踢出
        fun onUserKickedByOtherDevice(){}

    }

    companion object{
        val instance by lazy { ImStatusNotify() }
    }
}