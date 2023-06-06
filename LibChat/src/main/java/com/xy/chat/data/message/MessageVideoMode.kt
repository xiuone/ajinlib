package com.xy.chat.data.message

import com.xy.chat.data.message.base.MessageBaseFileMode
import com.xy.chat.type.MessageTypeEnum


/**
 * width：图片的宽度
 * height：图片的高度
 * duration：时间长度
 */
class MessageVideoMode (msgId:String, form:String, messageType :MessageTypeEnum, messageTime:Long):
    MessageBaseFileMode(msgId,form, messageType, messageTime){
    var width = 0
    var height = 0
    var duration: Long = 0

    override fun isCompleteSame(other: Any?): Boolean {
        if (other !is MessageVideoMode)return false
        return width == other.width &&
                height == other.height &&
                duration == other.duration && super.equals(other)
    }

    override fun onCreateRecyclerType(): Int = MessageTypeEnum.Video.type
}