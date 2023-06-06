package com.xy.chat.data.message.base

import com.xy.base.listener.CheckSameListener
import com.xy.chat.type.MessageTypeEnum

/**
 * servicePath 服务器地址
 * expire 过期时间
 * fileName 文件名字
 * fileSize 文件大小
 * getThumb 本地地址
 */
abstract class MessageBaseFileMode (msgId:String, form:String, messageType :MessageTypeEnum, messageTime:Long):
    MessageBaseMode(msgId,form, messageType, messageTime), CheckSameListener {
    var local: String ?= null
    var fileSize: Long = 0
    var fileName: String? = null
    var expire: Long = 0
    var servicePath:String?=null

    fun getThumb():String?{
        return local?:servicePath
    }

    override fun isCompleteSame(other: Any?): Boolean {
        if (other !is MessageBaseFileMode)return false
        return fileSize == other.fileSize &&
                fileName == other.fileName &&
                expire == other.expire &&
                servicePath == other.servicePath &&
                super.equals(other)
    }
}