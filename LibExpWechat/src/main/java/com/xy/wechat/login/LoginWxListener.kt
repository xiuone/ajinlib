package com.xy.wechat.login

interface LoginWxListener {
    fun loginWx(openId:String?,nickName:String?,sex:String?,headImgUrl:String)
}