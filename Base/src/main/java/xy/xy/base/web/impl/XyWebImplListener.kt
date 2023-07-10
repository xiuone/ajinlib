package xy.xy.base.web.impl

import android.view.View
import android.webkit.WebView
import xy.xy.base.web.XyWebView

interface XyWebImplListener {
    fun onCreateXyWebView():XyWebView
    fun onCreateLoadView():View
    fun onCreateWebView():WebView
}