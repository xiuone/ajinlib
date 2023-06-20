package com.xy.base.web.impl

import android.view.View
import android.webkit.WebView
import com.xy.base.web.XyWebView

interface XyWebImplListener {
    fun onCreateXyWebView():XyWebView
    fun onCreateLoadView():View
    fun onCreateWebView():WebView
}