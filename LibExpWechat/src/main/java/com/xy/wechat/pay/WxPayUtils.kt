package com.xy.wechat.pay

import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.modelpay.PayReq
import com.xy.base.utils.ContextHolder
import com.xy.base.utils.exp.getResString
import com.xy.wechat.R
import com.xy.wechat.WxManger

object WxPayUtils {
    /**
     * 微信原生支付
     */
    fun startWechatPay(appId:String?,partnerid:String?,prepayid:String?,noncestr:String?,timestamp:String?,sign:String?){
        // 将该app注册到微信
        WxManger.wxApi.registerApp(WxManger.wxAppId)
        val request = PayReq()
        request.appId = appId
        request.partnerId = partnerid
        request.prepayId = prepayid
        request.packageValue ="Sign=WXPay"
        request.nonceStr = noncestr
        request.timeStamp = timestamp
        request.sign = sign
        WxManger.wxApi.sendReq(request)
    }

    /**
     * 微信支付（小程序）
     */
    fun startWechatPay(path: String?)  = startWechatPay(ContextHolder.getContext()?.getResString(R.string.wx_app_applet),path)

    /**
     * 微信支付（小程序）
     */
    fun startWechatPay(userName:String?,path: String?) {
        WxManger.wxApi.registerApp(WxManger.wxAppId)
        val req = WXLaunchMiniProgram.Req()
        req.userName = userName // 填小程序原始id
        req.path = path //拉起小程序页面的可带参路径，不填默认拉起小程序首页
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE // 可选打开 开发版，体验版和正式版
        WxManger.wxApi.sendReq(req)
    }

}