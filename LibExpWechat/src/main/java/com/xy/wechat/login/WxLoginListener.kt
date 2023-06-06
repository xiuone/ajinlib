package com.xy.wechat.login

import com.tencent.mm.opensdk.modelmsg.SendAuth

interface WxLoginListener {
    fun onWxLoginScuCallBack(resp: SendAuth.Resp)
    fun onWxLoginCancel()
}