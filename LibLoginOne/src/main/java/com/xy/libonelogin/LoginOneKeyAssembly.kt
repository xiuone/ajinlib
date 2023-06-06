package com.xy.libonelogin

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.mobile.auth.gatewayauth.*
import com.mobile.auth.gatewayauth.model.TokenRet
import com.xy.base.assembly.load.BaseAssemblyLoadDialog
import com.xy.base.utils.Logger
import com.xy.base.utils.exp.dp2px
import com.xy.base.utils.exp.showToast
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

/**
 *@author:weng
 *@data:2022/3/25
 *@description:一键登录
 */
class LoginOneKeyAssembly(view: LoginOneKeyAssemblyView): BaseAssemblyLoadDialog<LoginOneKeyAssemblyView>(view),TokenResultListener {

    private val authHelper by lazy { PhoneNumberAuthHelper.getInstance(view.getCurrentAct(), this) }

    //     一键登录
     fun oneKeyLogin() {
        showLoad()
        authHelper?.reporter?.setLoggerEnable(BuildConfig.DEBUG)
        authHelper?.setAuthSDKInfo(this.view?.onsetAuthSDKInfoId())
        configAuthPage()
       // SDK环境检查函数，检查终端是否⽀持号码认证，通过TokenResultListener返回code
       //  type 1：本机号码校验 2: ⼀键登录
       //  600024 终端⽀持认证
       //  600013 系统维护，功能不可⽤
        authHelper?.checkEnvAvailable(PhoneNumberAuthHelper.SERVICE_TYPE_LOGIN)
    }

    private fun configAuthPage() {
        authHelper.setUIClickListener { code, _, jsonString ->
            var jsonObj =  JSONObject()
            try {
                if (!TextUtils.isEmpty(jsonString)) {
                    jsonObj = JSONObject(jsonString)
                }
            } catch (e: JSONException) {
            }
            when (code) {
                ResultCode.CODE_ERROR_USER_CANCEL -> {
                    Logger.e( "点击了授权页默认返回按钮")
                    authHelper.quitLoginPage()
                }
                ResultCode.CODE_ERROR_USER_SWITCH -> Logger.e( "点击了授权页默认切换其他登录方式")
                ResultCode.CODE_ERROR_USER_LOGIN_BTN ->
                    if (!jsonObj.optBoolean("isChecked")) {
                        getContext()?.showToast(this.view?.onErrorUserLoginBtn())
                    }
                ResultCode.CODE_ERROR_USER_CHECKBOX -> Logger.e("checkbox状态变为" + jsonObj!!.optBoolean("isChecked"))
                ResultCode.CODE_ERROR_USER_PROTOCOL_CONTROL -> Logger.e("点击协议，" + "name: " + jsonObj!!.optString("name") + ", url: " + jsonObj.optString("url"))
                else -> {}
            }
        }
        authHelper.removeAuthRegisterXmlConfig()
        authHelper.removeAuthRegisterViewConfig()
        //添加自定义切换其他登录方式
        authHelper.addAuthRegistViewConfig("switch_msg", AuthRegisterViewConfig.Builder()
            .setView(initSwitchView(350))
            .setRootViewId(AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_BODY)
            .setCustomInterface {
                view?.onOneLoginOtherLoginWay()
                authHelper.quitLoginPage()
            }.build())
        var authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        if (Build.VERSION.SDK_INT == 26) {
            authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_BEHIND
        }
        authHelper.setAuthUIConfig(AuthUIConfig.Builder()
            .setAppPrivacyOne(this.view?.onAppPrivacyOne(), this.view?.onAppPrivacyOneUrl())
            .setAppPrivacyTwo(this.view?.onAppPrivacyTwo(), this.view?.onAppPrivacyTwoUrl())
            .setAppPrivacyColor(this.view?.onAppPrivacyDefaultColor()?:Color.GRAY, this.view?.onAppPrivacyColor()?:Color.parseColor("#666666")) //隐藏默认切换其他登录方式
            .setSwitchAccHidden(true) //隐藏默认Toast
            .setLogBtnToastHidden(true) //沉浸式状态栏
            .setNavColor(this.view?.onNavColor()?:Color.parseColor("#ffffff"))
            .setNavText(this.view?.onNavText())
            .setNavTextColor(this.view?.onNavTextColor()?:Color.parseColor("#000000"))
            .setStatusBarColor(Color.WHITE)
            .setWebViewStatusBarColor(Color.parseColor("#FFFFFF"))
            .setNavReturnImgDrawable(this.view?.onBackDrawable())
            .setLightColor(true)
            .setWebNavTextSizeDp(20) //图片或者xml的传参方式为不包含后缀名的全称 需要文件需要放在drawable或drawable-xxx目录下 in_activity.xml, mytel_app_launcher.png
            .setVendorPrivacyPrefix("《")
            .setVendorPrivacySuffix("》")
            .setLogBtnBackgroundDrawable(this.view?.onLoginDrawable())
            .setScreenOrientation(authPageOrientation)
            .setSloganText(this.view?.onSloganText())
            .setSloganTextColor(this.view?.onSloganTextColor()?:Color.parseColor("#000000"))
            .create())
    }


    private fun initSwitchView(marginTop: Int): View {
        val switchTV = TextView(getContext())
        val mLayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, getContext()?.dp2px( 50f)?:0)
        //一键登录按钮默认marginTop 270dp
        mLayoutParams.setMargins(0, getContext()?.dp2px(marginTop.toFloat())?:0, 0, 0)
        mLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
        //        切换到其他登录方式提示
//        switchTV.setText(R.string.switch_msg);
        switchTV.setTextColor(Color.BLACK)
        switchTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.0f)
        switchTV.layoutParams = mLayoutParams
        return switchTV
    }


    //    获取Token成功
    override fun onTokenSuccess(s: String) {
        Log.e("tag", "获取token成功：$s")
        var tokenRet: TokenRet?
        try {
            tokenRet = TokenRet.fromJson(s)
            when {
                ResultCode.CODE_START_AUTHPAGE_SUCCESS == tokenRet.code -> {
                    dismiss()
                    Log.i("TAG", "唤起授权页成功：$s")
                }
                ResultCode.CODE_ERROR_ENV_CHECK_SUCCESS == tokenRet.code -> {
                //    sim卡可以用，则进行唤起页面操作
                    authHelper?.getLoginToken(view?.getCurrentAct(),5000)
                }
                ResultCode.CODE_SUCCESS == tokenRet.code -> {
                    Log.i("TAG", "获取token成功：$s")
                    authHelper?.hideLoginLoading()
                    authHelper?.quitLoginPage()
        //               上传服务器操作
                    val token = tokenRet.token
                    if (token != null){
                        view?.loginOneKey(token)
                    }else{
                        onTokenFailed("token 为null")
                    }
                }else->{
                    dismiss()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //    获取Token失败
    override fun onTokenFailed(s: String) {
        dismiss()
        Log.e("tag", "获取token失败：$s")
        authHelper?.hideLoginLoading()
        var tokenRet: TokenRet
        try {
            tokenRet = TokenRet.fromJson(s)
            if (ResultCode.CODE_ERROR_USER_CANCEL == tokenRet.code) {
            // 跳转到手机号登录页面（用户取消登录操作）
                authHelper?.quitLoginPage()
                authHelper?.removeAuthRegisterXmlConfig()
                authHelper?.removeAuthRegisterViewConfig()
            } else {
               //  跳转到手机号登录页面
                view?.onOneLoginOtherLoginWay()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}