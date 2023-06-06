package com.xy.chat.notify.recent

import  com.xy.base.utils.notify.NotifyBase
import com.xy.chat.data.recent.AppRecentContactMode
import com.xy.chat.type.ChatSessionEnum

class ImRecentNotify : NotifyBase<ImRecentNotify.ImRecentListener>() {
    //最近聊天记录回调
    fun onRecentContactCallBack(data:MutableList<AppRecentContactMode>) = findItem { it.onRecentContactCallBack(data) }
    //获取最近聊天信息异常回调
    fun onRecentContactErrorCallBack() = findItem { it.onRecentContactErrorCallBack() }
    //更新聊天信息
    fun onRecentContactUpdateItem(item: AppRecentContactMode) = findItem { it.onRecentContactUpdateItem(item) }
    //删除所有的最近聊天记录
    fun onRecentContactDeleteAll() = findItem { it.onRecentContactDeleteAll() }
    //删除所有的最近聊天记录
    fun onRecentContactDeleteItem(contactId:String,chatSessionEnum: ChatSessionEnum) = findItem { it.onRecentContactDeleteItem(contactId,chatSessionEnum) }


    interface ImRecentListener {
        //最近聊天记录回调
        fun onRecentContactCallBack(data:MutableList<AppRecentContactMode>){}
        //获取最近聊天信息异常回调
        fun onRecentContactErrorCallBack(){}
        //更新聊天信息
        fun onRecentContactUpdateItem(item: AppRecentContactMode){}
        //删除所有的最近聊天记录
        fun onRecentContactDeleteAll(){}
        //删除所有的最近聊天记录
        fun onRecentContactDeleteItem(contactId:String,chatSessionEnum: ChatSessionEnum){}
    }



    companion object{
        val instance by lazy { ImRecentNotify() }
    }
}