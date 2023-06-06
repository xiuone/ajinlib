package com.xy.qq.login

import com.tencent.tauth.IUiListener
import com.xy.base.utils.exp.getResString
import com.xy.base.utils.exp.showToast
import com.xy.qq.QQManger
import com.xy.qq.R
import org.json.JSONObject

/**
 * 登录    需要添加的button 和需要做的事  这是必须的
 */
class LoginAssemblyInfo(view: LoginAssemblyView) : LoginAssemblyBase(view),IUiListener{

    override fun onComplete(json: Any?) {
        if (json is JSONObject){
            val openId = QQManger.mTencent.openId
            val nickName = json.getString("nickname")
            val sex = json.getString("sex")
            val headImgUrl = json.getString("figureurl_qq_2")
            view?.loginQQ(openId,nickName,sex,headImgUrl)
        }else{
            dismiss()
            getContext()?.showToast(getContext()?.getResString(R.string.qq_login_error))
        }
    }
}