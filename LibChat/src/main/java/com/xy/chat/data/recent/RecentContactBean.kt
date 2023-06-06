package com.xy.chat.data.recent

import com.xy.base.listener.CheckSameListener
import com.xy.chat.data.message.base.MessageBaseMode
import com.xy.chat.type.ChatSessionEnum

/**
 * contactId:（好友帐号，群ID等）
 * form:（获取与该联系人的最后一条消息的发送方的帐号
 * sessionType:获取会话类型
 * messageType:获取最近一条消息的消息类型
 * messageStatus:获取最近一条消息状态
 * content:获取最近一条消息的缩略内容
 * messageTime:获取最近一条消息的时间，单位为ms
 * unReadNumber:获取该联系人的未读消息条数
 * extContent:扩展信息
 */
data class RecentContactBean(val contactId:String, val sessionType: ChatSessionEnum, var lastMessage: MessageBaseMode, var unReadNumber:Long = 0L):
    CheckSameListener {

    override fun isCompleteSame(other: Any?): Boolean {
        if (other !is RecentContactBean)return false
        return contactId == other.contactId &&
                sessionType == other.sessionType &&
                lastMessage.isCompleteSame(other.lastMessage) &&
                unReadNumber == other.unReadNumber
    }

    override fun equals(other: Any?): Boolean {
        if (other !is RecentContactBean)return false
        return contactId == other.contactId &&
                sessionType == other.sessionType
    }
}