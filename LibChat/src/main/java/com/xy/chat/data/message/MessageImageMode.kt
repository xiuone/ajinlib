package com.xy.chat.data.message

import com.xy.chat.data.message.base.MessageBaseFileMode
import com.xy.chat.type.MessageTypeEnum

/**
 * width：图片的宽度
 * height：图片的高度
 */
class MessageImageMode (msgId:String, form:String, messageType :MessageTypeEnum, messageTime:Long):
    MessageBaseFileMode(msgId,form, messageType, messageTime){
    var width = 0
    var height = 0

    override fun isCompleteSame(other: Any?): Boolean {
        if (other !is MessageImageMode)return false
        return width == other.width &&
                height == other.height && super.equals(other)
    }

    override fun onCreateRecyclerType(): Int = MessageTypeEnum.Image.type
}