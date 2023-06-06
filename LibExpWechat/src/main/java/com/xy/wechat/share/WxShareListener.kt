package com.xy.wechat.share

import com.tencent.mm.opensdk.modelmsg.SendAuth

interface WxShareListener {
    fun onWxShareScuCallBack()
}