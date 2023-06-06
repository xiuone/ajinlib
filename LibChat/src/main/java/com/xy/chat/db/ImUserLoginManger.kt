package com.xy.chat.db

import android.content.Context
import com.xy.base.utils.exp.getSpString
import com.xy.base.utils.exp.setSpString

object ImUserLoginManger {
    private val imUserLogin = "imUserLogin"
    fun getImUserLoginToken(context: Context?):String?{
        return context?.getSpString(imUserLogin,"")?:""
    }
    fun saveImUserLoginToken(context: Context?,token:String?){
        context?.setSpString(imUserLogin,token)
    }
}