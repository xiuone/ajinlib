package com.xy.chat.type

enum class MessageTypeEnum(val type:Int) {
    UnKnow(0),//未知消息
    Text(1),//文本
    Image(2),//图片
    Video(3),//视频
    Voice(4),//语音
    Location(5),//定位
    File(6),//文件
    AvChat(7),//音视频消息
    Red(8),//红包
    Finger(9),//猜拳
    Dice(10),//骰子
    Custom(11),//自定义
}