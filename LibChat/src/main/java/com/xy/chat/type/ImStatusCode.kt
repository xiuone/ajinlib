package com.xy.chat.type

enum class ImStatusCode {
    INVALID,//未定义
    UNLOGIN,//未登录/登录失败
    NET_BROKEN,//网络连接已断开
    CONNECTING,//正在连接服务器
    LOGINING,//正在登录中
    SYNCING,//正在同步数据
    LOGINED,//已成功登录
    KICKOUT,//被其他端的登录踢掉
    KICK_BY_OTHER_CLIENT,//被同时在线的其他端主动踢掉
    FORBIDDEN,//被服务器禁止登录
    VER_ERROR,//客户端版本错误
    PWD_ERROR,//用户名或密码错误
    DATA_UPGRADE,//数据升级
}