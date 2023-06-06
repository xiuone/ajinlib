package com.xy.chat.provider

import com.luck.picture.lib.entity.LocalMedia
import com.xy.chat.type.ChatSessionEnum

interface ImChatListener {
    /**
     * 发送文件
     */
    fun sendFileMessage(path:LocalMedia,conversationId:String,sessionEnum:ChatSessionEnum,exp:Any?)
    /**
     * 发送文件
     */
    fun sendFileMessage(path:String,conversationId:String,sessionEnum:ChatSessionEnum,exp:Any?)
    /**
     * 发送文本信息
     */
    fun sendTextMessage(content:String,conversationId:String,sessionEnum:ChatSessionEnum,exp:Any?)

    fun sendLocalMessage()

    fun clearHistory(sessionId:String?,type:ChatSessionEnum?)
}