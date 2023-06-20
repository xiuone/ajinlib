package com.xy.base.web

import android.graphics.Bitmap
import android.webkit.WebView

interface XyWebViewListener {
    fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?){}
    fun onPageFinished(view: WebView?, url: String?){}
}