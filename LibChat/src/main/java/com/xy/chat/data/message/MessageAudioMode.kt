package com.xy.chat.data.message

import com.xy.chat.data.message.base.MessageBaseFileMode
import com.xy.chat.type.MessageTypeEnum

/**
 * width：图片的宽度
 * height：图片的高度
 */
class MessageAudioMode (msgId:String, form:String, messageType :MessageTypeEnum, messageTime:Long):
    MessageBaseFileMode(msgId,form, messageType, messageTime){
    var duration: Long = 0

    override fun isCompleteSame(other: Any?): Boolean {
        if (other !is MessageAudioMode)return false
        return duration == other.duration && super.equals(other)
    }

    override fun onCreateRecyclerType(): Int = MessageTypeEnum.Voice.type
}