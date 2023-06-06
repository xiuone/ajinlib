package com.xy.chat.data.message.base

import com.xy.base.listener.CheckSameListener
import com.xy.base.widget.recycler.adapter.RecyclerMultiListener
import com.xy.chat.type.MessageStatusEnum
import com.xy.chat.type.MessageTypeEnum

/**
 * messageId:（好友帐号，群ID等）
 * form:获取发送方的帐号
 * to:获取接收方的帐号
 * sessionType:获取会话类型
 * messageType:获取最近一条消息的消息类型
 * messageStatus:获取最近一条消息状态
 * messageTime:获取最近一条消息的时间，单位为ms
 * expInfo:扩展信息
 */
abstract class MessageBaseMode (val messageId:String, val form:String, val messageType : MessageTypeEnum, val messageTime:Long):
    CheckSameListener , RecyclerMultiListener {
    var messageStatus: MessageStatusEnum = MessageStatusEnum.Success
    val expInfo:Any? = null

    override fun equals(other: Any?): Boolean {
        if (other !is MessageBaseMode)return false
        return messageId == other.messageId
    }

    override fun isCompleteSame(other: Any?): Boolean {
        if (other !is MessageBaseMode)return false
        return messageId == other.messageId &&
                form == other.form &&
                messageType == other.messageType &&
                messageTime == other.messageTime &&
                messageStatus == other.messageStatus &&
                expInfo == other.expInfo
    }
}