package com.xy.wechat.login

import androidx.lifecycle.LifecycleOwner
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.xy.base.assembly.load.BaseAssemblyLoadDialog
import com.xy.base.utils.Logger
import com.xy.base.utils.exp.getResString
import com.xy.base.utils.exp.setOnClick
import com.xy.base.utils.exp.showToast
import com.xy.wechat.R
import com.xy.wechat.WxManger
import org.json.JSONObject


class WxLoginAssembly(view: WxLoginAssemblyView) : BaseAssemblyLoadDialog<WxLoginAssemblyView>(view),
    WxLoginListener {
    private val paramsOpenIdKey by lazy { "openid" }
    private val paramsNicknameKey by lazy { "nickname" }
    private val paramsSexKey by lazy { "sex" }
    private val paramsHeadImgUrlKey by lazy { "headimgurl" }
    
    override fun onCreate(owner: LifecycleOwner?) {
        super.onCreate(owner)
        view?.onCreateWxLoginView()?.setOnClick{
            WxManger.wxApi.registerApp(WxManger.wxAppId)
            if (!WxManger.wxApi.isWXAppInstalled){
                getContext()?.showToast(getContext()?.getResString(R.string.wx_please_install))
                return@setOnClick
            }
            showLoad()
            val req = SendAuth.Req()
            req.scope = "snsapi_userinfo"
            req.state = "app_wechat"
            WxManger.wxLoginListener = this
            WxManger.wxApi.sendReq(req)
        }
    }

    /**
     * 登录成功---获取token
     */
    override fun onWxLoginScuCallBack(resp: SendAuth.Resp) {
        val context = getContext()?:return
        val params = HashMap<String,String>()
        params["appid"] = context.getString(R.string.wx_app_id)
        params["secret"] = context.getString(R.string.wx_app_secret)
        params["code"] = resp.code
        params["grant_type"] = "authorization_code"
        OkGo.getInstance().cancelTag(WxManger.wxAccessTokenUrl)
        OkGo.getInstance().cancelTag(WxManger.wxGetUserInfo)
        OkGo.get<String>(WxManger.wxAccessTokenUrl).tag(WxManger.wxAccessTokenUrl)
            .params("appid", WxManger.wxAppId)
            .params("secret", WxManger.wxAppSecret)
            .params("code",resp.code)
            .params("grant_type","authorization_code")
            .execute(object :StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    try {
                        val body = response?.body()?:""
                        if (body.isEmpty()){
                            getContext()?.showToast(getContext()?.getResString(R.string.wx_login_error))
                            dismiss()
                            return
                        }
                        Logger.d("-----wxAccessToken获取到的json数据-----$body")
                        val jsonObject = JSONObject(body)
                        val accessToken = jsonObject.getString("access_token")
                        val openid = jsonObject.getString(paramsOpenIdKey)
                        if (accessToken.isNullOrEmpty() || openid.isNullOrEmpty()){
                            getContext()?.showToast(getContext()?.getResString(R.string.wx_login_error))
                            dismiss()
                            return
                        }
                        getUserInfo(accessToken, openid)
                        return
                    } catch (e:Exception ) {
                        e.printStackTrace()
                    }
                    getContext()?.showToast(getContext()?.getResString(R.string.wx_login_error))
                    dismiss()
                }
            })

    }
    /**
     * 登录成功---获取用户信息
     */
    private fun getUserInfo(accessToken:String,openid:String){
        OkGo.getInstance().cancelTag(WxManger.wxAccessTokenUrl)
        OkGo.getInstance().cancelTag(WxManger.wxGetUserInfo)
        OkGo.get<String>(WxManger.wxGetUserInfo).tag(WxManger.wxGetUserInfo)
            .params("openid",openid)
            .params("access_token",accessToken)
            .execute(object :StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    val body = response?.body()?:""
                    if (body.isEmpty()){
                        getContext()?.showToast(getContext()?.getResString(R.string.wx_login_error))
                        dismiss()
                        return
                    }

                    try {
                        val jsonObject = JSONObject(body)
                        val openid = jsonObject.getString(paramsOpenIdKey)
                        val nickName = jsonObject.getString(paramsNicknameKey)
                        val sex = jsonObject.getString(paramsSexKey)
                        val headImgUrl = jsonObject.getString(paramsHeadImgUrlKey)
                        view?.loginWx(openid,nickName,sex,headImgUrl)
                        return
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    getContext()?.showToast(getContext()?.getResString(R.string.wx_login_error))
                    dismiss()
                }
            })
    }

    /**
     * 取消登录
     */
    override fun onWxLoginCancel() {
        getContext()?.showToast(getContext()?.getResString(R.string.wx_login_cancel))
    }

    override fun onDestroyed(owner: LifecycleOwner) {
        super.onDestroyed(owner)
        OkGo.getInstance().cancelTag(WxManger.wxAccessTokenUrl)
        OkGo.getInstance().cancelTag(WxManger.wxGetUserInfo)
    }
}