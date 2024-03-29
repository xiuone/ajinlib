package xy.xy.base.widget.shadow.view

import android.content.Context
import android.graphics.Canvas
import android.text.TextUtils
import android.util.AttributeSet
import android.webkit.WebSettings
import android.webkit.WebView
import xy.xy.base.widget.shadow.ShadowBuilder
import xy.xy.base.widget.shadow.impl.OnDrawImpl
import xy.xy.base.widget.shadow.impl.ShadowBuilderImpl

open class ShadowWebView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    WebView(context, attrs)  {
    val shadowBuilderImpl: ShadowBuilderImpl by lazy { ShadowBuilderImpl(ShadowBuilder(this, attrs)) }
    protected val onDrawImpl by lazy { onCreateDrawImpl() }

    init {
        onDrawImpl.initView()
        setSetting()
    }
    open fun onCreateDrawImpl() :OnDrawImpl = OnDrawImpl(this, shadowBuilderImpl)

    private fun setSetting(){
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
        settings.textZoom = 100
        val userAgent: String = getDefaultUserAgent()
        val curUa = settings.userAgentString

        if (!TextUtils.equals(userAgent, curUa)) {
            settings.userAgentString = userAgent
        }
    }

    override fun onResume() {
        super.onResume()
        setSetting()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        val newLeft = onDrawImpl.getPaddingLeft()+left
        val newRight = onDrawImpl.getPaddingRight()+right
        val newTop = onDrawImpl.getPaddingTop()+top
        val newBottom = onDrawImpl.getPaddingBottom()+bottom
        super.setPadding(newLeft, newTop, newRight, newBottom)
    }


    private fun getDefaultUserAgent(): String {
        var userAgent =  WebSettings.getDefaultUserAgent(context)

        val sb = StringBuffer()
        var i = 0
        val length = userAgent.length
        while (i < length) {
            val c = userAgent[i]
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", c.code))
            } else {
                sb.append(c)
            }
            i++
        }
        return sb.toString()
    }


    override fun onDraw(canvas: Canvas) {
        onDrawImpl.onDraw(canvas)
        super.onDraw(canvas)
        onDrawImpl.onDrawStoke(canvas)
    }

    fun loadDataWithBaseURL(html:String?) {
        if (html == null)return
        super.loadDataWithBaseURL(null,html,"text/html","UTF-8","about:blank")
    }

    /**
     * 计算webView内容的真实高度
     * @return
     */
    protected fun getMeasureContentHeight() :Int{
        return computeVerticalScrollRange()
    }
}