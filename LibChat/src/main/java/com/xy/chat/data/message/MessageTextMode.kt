package com.xy.chat.data.message

import com.xy.chat.data.message.base.MessageBaseMode
import com.xy.chat.type.MessageTypeEnum

class MessageTextMode (msgId:String,form:String,messageType :MessageTypeEnum,messageTime:Long):
    MessageBaseMode(msgId,form, messageType, messageTime){

    var content:String?=null

    override fun isCompleteSame(other: Any?): Boolean {
        if (other !is MessageTextMode)return false
        return content == other.content && super.equals(other)
    }

    override fun onCreateRecyclerType(): Int = MessageTypeEnum.Text.type
}