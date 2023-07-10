package com.xy.wechat

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import xy.xy.base.utils.Logger
import xy.xy.base.utils.exp.getResString
import xy.xy.base.utils.exp.showToast

abstract class WxBaseBackActivity : FragmentActivity(),IWXAPIEventHandler {
    private val wxAppId by lazy { getResString(R.string.wx_app_id) }
    private val wxApi: IWXAPI by lazy { WxManger.wxApi }

    override fun onReq(req: BaseReq?) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wxApi.registerApp(wxAppId)
        handlerIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?:return
        handlerIntent(intent)
    }

    private fun handlerIntent(intent: Intent){
        setIntent(intent)
        wxApi.handleIntent(intent,this)
    }

    override fun onResp(resp: BaseResp?) {
        Logger.e("======微信回调状态:${resp?.errCode}")
        when(resp?.errCode){
            BaseResp.ErrCode.ERR_OK->{
                if (resp is SendAuth.Resp){
                    Logger.e("======微信登录成功:${WxManger.wxLoginListener}")
                    WxManger.wxLoginListener?.onWxLoginScuCallBack(resp)
                }else{
                    Logger.e("======微信分享成功")
                    WxManger.wxShareListener?.onWxShareScuCallBack()
                    showToast(getResString(shareSucStringResId()))
                }
            }
            BaseResp.ErrCode.ERR_USER_CANCEL-> {
                if (resp is SendAuth.Resp){
                    Logger.e("======微信取消登录:${WxManger.wxLoginListener}")
                    WxManger.wxLoginListener?.onWxLoginCancel()
                }else {
                    showToast(getResString(shareCancelStringResId()))
                }
            }
        }
        WxManger.wxLoginListener = null
        WxManger.wxShareListener = null
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        WxManger.wxLoginListener = null
        WxManger.wxShareListener = null
    }

    /**
     * 用户取消
     */
    abstract fun shareCancelStringResId():Int

    /**
     * 分享成功
     */
    abstract fun shareSucStringResId():Int

}