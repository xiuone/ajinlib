package com.xy.qq.login

import com.tencent.connect.UserInfo
import com.tencent.tauth.IUiListener
import xy.xy.base.utils.exp.getResString
import xy.xy.base.utils.exp.setOnClick
import xy.xy.base.utils.exp.showToast
import com.xy.qq.QQManger
import com.xy.qq.R
import org.json.JSONObject

/**
 * 登录    需要添加的button 和需要做的事  这是必须的
 */
class LoginAssembly(view: LoginAssemblyView) : LoginAssemblyBase(view),IUiListener{
    private val paramsAccessTokenKey by lazy { "access_token" }
    private val paramsOpenIdKey by lazy { "openid" }
    private val paramsExpiresInKey by lazy { "expires_in" }


    //点击登录
    private val loginButton by lazy { this.view?.onCreateQQLoginView() }
    private val info by lazy { LoginAssemblyInfo(view) }

    override fun onCreateInit() {
        super.onCreateInit()
        loginButton?.setOnClick{
            showLoad()
            QQManger.mTencent.login(view?.getCurrentAct(),"all",this)
        }
    }

    override fun onComplete(json: Any?) {
        if (json is JSONObject){
            val openID = json.getString(paramsOpenIdKey)
            val accessToken = json.getString(paramsAccessTokenKey)
            val expiresIn = json.getString(paramsExpiresInKey)
            QQManger.mTencent.openId = openID
            QQManger.mTencent.setAccessToken(accessToken, expiresIn)
            val mUserInfo = UserInfo(getContext()?.applicationContext, QQManger.mTencent?.qqToken)
            mUserInfo.getUserInfo(info)
        }else{
            dismiss()
            getContext()?.showToast(getContext()?.getResString(R.string.qq_login_error))
        }
    }

}