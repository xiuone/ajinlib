package com.xy.base.web.impl

import android.graphics.Bitmap
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.xy.base.web.XyWebViewListener

open class XyWebViewClient: WebViewClient() {
    private var listener:XyWebImplListener ?= null
    private var webListener:XyWebViewListener?=null
    private val webView by lazy { listener?.onCreateWebView() }
    private val loadView by lazy { listener?.onCreateLoadView() }
    private var isFinish = false;

    fun onCreate(){
        webView?.webViewClient = this
    }

    fun bindWebListener(webListener:XyWebViewListener):XyWebViewClient{
        this.webListener = webListener
        return this
    }

    fun bindWebImplListener(listener:XyWebImplListener):XyWebViewClient{
        this.listener = listener
        return this
    }


    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        isFinish = true
        loadView?.visibility = View.GONE
        webListener?.onPageFinished(webView,url)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        if (!isFinish){
            loadView?.visibility = View.VISIBLE
        }
        webListener?.onPageStarted(view,url,favicon)
    }

    fun onDestroy(){
        webListener = null
        listener = null
    }
}