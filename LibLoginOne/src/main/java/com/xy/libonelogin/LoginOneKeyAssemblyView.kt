package com.xy.libonelogin

import android.graphics.drawable.Drawable
import com.xy.base.assembly.load.BaseAssemblyViewLoadDialog

interface LoginOneKeyAssemblyView: BaseAssemblyViewLoadDialog ,LoginOneKeyListener{
    fun onOneLoginOtherLoginWay()
    fun onsetAuthSDKInfoId():String?
    fun onErrorUserLoginBtn():String?
    fun onBackDrawable():Drawable?
    fun onLoginDrawable():Drawable?
    fun onSloganText():String?
    fun onSloganTextColor():Int?
    fun onNavColor():Int?
    fun onNavText():String?
    fun onNavTextColor():Int?
    fun onAppPrivacyDefaultColor():Int?
    fun onAppPrivacyColor():Int?
    fun onAppPrivacyOne():String?
    fun onAppPrivacyOneUrl():String?
    fun onAppPrivacyTwo():String?
    fun onAppPrivacyTwoUrl():String?
}