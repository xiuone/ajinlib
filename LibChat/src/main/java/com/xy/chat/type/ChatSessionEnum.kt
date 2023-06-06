package com.xy.chat.type

enum class ChatSessionEnum(val type:Int) {
    P2P(1),//单聊
    Team(2),//群聊
    System(3),//系统消息
    ChatRoom(4),//聊天室
}