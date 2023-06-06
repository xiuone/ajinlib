package com.xy.chat.provider

import com.xy.chat.type.ChatSessionEnum

interface IMRecentListener {
    /**
     * 删除最近搜有消息
     */
    fun onDeleteAllRecentContact()

    /**
     * 删除某个聊天记录
     */
    fun onDeleteRecentContact(account:String?,chatSessionEnum:ChatSessionEnum)
    /**
     * 获取最近聊天记录
     */
    fun getRecentContact()
}