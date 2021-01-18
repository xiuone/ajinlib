package com.xy.baselib.ui.act

import android.content.Intent
import android.os.Build
import android.os.Debug
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.xy.baselib.mvp.controller.WebController


abstract class BaseWebAct :BaseAct(){
    private var webController: WebController?=null
    fun getWebController():WebController?{
        if (webController == null )
            webController = WebController(this)
        return webController
    }

    fun initWebView(webView: WebView?, fl_video:FrameLayout?,debug: Boolean){
        if (webView == null)return
        getWebController()?.setWebView(this,webView,fl_video,debug)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getWebController()?.onActivityResult(requestCode,resultCode,data)
    }


    fun load(webView: WebView?,str:String?){
        if (str == null)
            getWebController()?.loadDataWithBaseURL(webView,str)
        else
            getWebController()?.loadUrl(webView,str)
    }

}