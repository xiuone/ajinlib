package com.xy.qq.login

import com.tencent.tauth.IUiListener
import com.tencent.tauth.UiError
import xy.xy.base.assembly.load.BaseAssemblyLoadDialog
import xy.xy.base.utils.Logger
import xy.xy.base.utils.exp.getResString
import xy.xy.base.utils.exp.showToast
import com.xy.qq.R

/**
 * 登录    需要添加的button 和需要做的事  这是必须的
 */
abstract class LoginAssemblyBase(view: LoginAssemblyView) : BaseAssemblyLoadDialog<LoginAssemblyView>(view),IUiListener{

    override fun onError(uiError: UiError?) {
        dismiss()
        getContext()?.showToast(getContext()?.getResString(R.string.qq_login_error))
    }

    override fun onCancel() {
        dismiss()
        getContext()?.showToast(getContext()?.getResString(R.string.qq_login_cancel))
    }

    override fun onWarning(p0: Int) {
        Logger.d("===LoginAssembly====onWarning =====$p0")
    }

}