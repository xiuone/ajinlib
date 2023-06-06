package com.xy.chat.type

enum class MessageStatusEnum(val type:Int) {
    Draft(1),//草稿
    Sending(2),//发送中
    Success(3),//发送成功
    Fail(4),//发送失败
    Read(5),//已读
    Unread(6),//未读
}