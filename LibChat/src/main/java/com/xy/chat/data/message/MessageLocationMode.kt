package com.xy.chat.data.message

import com.xy.chat.data.message.base.MessageBaseFileMode
import com.xy.chat.type.MessageTypeEnum

/**
 * latitude：经度
 * longitude：纬度
 * address：地址信息
 */
class MessageLocationMode (msgId:String, form:String, messageType :MessageTypeEnum, messageTime:Long):
    MessageBaseFileMode(msgId,form, messageType, messageTime){
    var latitude = 0.0
    var longitude = 0.0
    var address: String? = null


    override fun isCompleteSame(other: Any?): Boolean {
        if (other !is MessageLocationMode)return false
        return latitude == other.latitude &&
                longitude == other.longitude &&
                address == other.address && super.equals(other)
    }

    override fun onCreateRecyclerType(): Int = MessageTypeEnum.Location.type
}