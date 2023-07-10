package com.xy.wechat

import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import xy.xy.base.utils.ContextHolder
import xy.xy.base.utils.exp.getResString
import com.xy.wechat.login.WxLoginListener
import com.xy.wechat.share.WxShareListener

object WxManger {

    val wxAppId by lazy { ContextHolder.getContext()?.getResString(R.string.wx_app_id)?:"" }
    val wxAppSecret by lazy { ContextHolder.getContext()?.getResString(R.string.wx_app_secret) }
    val wxApi: IWXAPI by lazy { WXAPIFactory.createWXAPI(ContextHolder.getContext(), wxAppId, true); }

    const val wxAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token"
    const val wxGetUserInfo = "https://api.weixin.qq.com/sns/userinfo"

    const val maxSize = 32 * 1024


    var wxLoginListener : WxLoginListener?=null
    var wxShareListener : WxShareListener?=null
}