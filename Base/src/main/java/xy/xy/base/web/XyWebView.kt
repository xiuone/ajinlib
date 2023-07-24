package xy.xy.base.web

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import xy.xy.base.R
import xy.xy.base.utils.Logger
import xy.xy.base.utils.config.font.FontManger
import xy.xy.base.web.agent.UserAgentObject
import xy.xy.base.web.impl.XyWebImplListener
import xy.xy.base.web.impl.XyWebViewClient

class XyWebView(context: Context,attrs: AttributeSet? = null) :FrameLayout(context, attrs),XyWebImplListener {
    private val webView by lazy { WebView(context) }
    private val loadView by lazy { FrameLayout(context) }
    private val videoView by lazy { FrameLayout(context) }
    private var client:XyWebViewClient?=null
    private var webListener :XyWebViewListener?=null
    init {
        val params = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        addView(webView,params)
        addView(loadView,params)
        addView(videoView,params)
        videoView.visibility = View.GONE
        addLoadView(LayoutInflater.from(context).inflate(R.layout.a_page_load,null))
        updateSetting()
        setWebClient(XyWebViewClient())
    }

    fun setWebClient(client: XyWebViewClient){
        this.client = client
        client.bindWebImplListener(this)
        client.onCreate()
    }

    fun bindWebListener(webViewListener: XyWebViewListener){
        this.webListener = webViewListener
        client?.bindWebListener(webViewListener)
    }

    fun loadUrl(url:String?) = webView.loadUrl(url?:"http://www.baidu.com")
    fun loadDataWithBaseURL(content:String?) = webView.loadDataWithBaseURL("about:blank", getHtmlData(content), "text/html", "utf-8", null)


    private fun getHtmlData(bodyHTML: String?): String {
        val head = """<!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>Title</title>
                    <style>
                        img{ max-width:100%;max-height:100%}  
                     </style>
                </head>
                <body>"""
        return "$head${bodyHTML?:""}</body>\n</html>"
    }


    private fun addLoadView(view:View?){
        if (view == null)return
        val parent = view.parent
        if (parent is ViewGroup){
            parent.removeView(view)
        }
        val params = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER
        loadView.removeAllViews()
        loadView.addView(view)
    }

    private fun updateSetting() {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.cacheMode = WebSettings.LOAD_DEFAULT //优先使用缓存
        settings.domStorageEnabled = true
        settings.setGeolocationEnabled(true)
        settings.savePassword = false
        settings.saveFormData = false
        settings.useWideViewPort = true
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        settings.loadWithOverviewMode = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.pluginState = WebSettings.PluginState.ON
        settings.allowFileAccessFromFileURLs = false
        settings.allowUniversalAccessFromFileURLs = false
        settings.mixedContentMode = WebSettings.LOAD_CACHE_ONLY
        val scale: Int = FontManger.instant.getWebTextZoom(context)
        if (scale != settings.textZoom) {
            settings.textZoom = scale
        }
        val userAgent: String = UserAgentObject.getCurrentUserAgent(context)
        val curUa = settings.userAgentString
        if (!TextUtils.equals(userAgent, curUa)) {
            Logger.d("curUa update")
            settings.userAgentString = userAgent
        }
        settings.blockNetworkImage = false
    }

    override fun onCreateXyWebView(): XyWebView  = this

    override fun onCreateLoadView(): View = loadView

    override fun onCreateWebView(): WebView = webView
}