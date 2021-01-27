package com.xy.baselib.mvp.controller

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.webkit.*
import android.widget.FrameLayout
import com.luck.picture.lib.config.PictureMimeType
import com.xy.baselib.mvp.impl.BaseImpl
import com.xy.baselib.utils.getOnePath
import com.xy.baselib.utils.getPath
import com.xy.baselib.utils.selectImg
import java.io.File


private const val FILE_CHOOSER_RESULT_CODE = 10000
private var uploadMessage: ValueCallback<Uri?>? = null
private var uploadMessageAboveL: ValueCallback<Array<Uri>>? = null
private var chooseMode:Int =  PictureMimeType.ofAll()

class WebController(private var webViewImpl: BaseImpl?) {
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FILE_CHOOSER_RESULT_CODE) { //处理返回的图片，并进行上传
            if (null == uploadMessage && null == uploadMessageAboveL) return
            val medias = getPath(resultCode,data)
            val path = getOnePath(medias)
            if (path != null){
                val file = File(path)
                if (file.exists()){
                    val result: Uri = Uri.fromFile(file)
                    val results: Array<Uri> = arrayOf(result)
                    uploadMessageAboveL?.onReceiveValue(results)
                    uploadMessage?.onReceiveValue(result)
                }
            }
            uploadMessage = null
            uploadMessageAboveL = null
        }
    }


    fun setWebView( activity:Activity,webView: WebView?, mLayout: FrameLayout?,debug:Boolean):WebSettings? {
        webView?.webChromeClient = MyWebChromeClient(mLayout,activity)
        webView?.webViewClient = MyWebViewClient(webViewImpl)
        webView?.overScrollMode = WebView.OVER_SCROLL_NEVER
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        val settings = webView?.settings
        if (getPhoneAndroidSDK() >= 14) { // 4.0需打开硬件加速
            activity.window.setFlags(0x1000000, 0x1000000)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(debug)
        }
        settings?.javaScriptEnabled = true
        settings?.cacheMode = WebSettings.LOAD_NO_CACHE
        settings?.domStorageEnabled = true
        settings?.setGeolocationEnabled(true)
        webView?.isVerticalScrollBarEnabled = false // 取消Vertical ScrollBar显示
        webView?.isHorizontalScrollBarEnabled = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings?.allowFileAccessFromFileURLs = true
        }
        settings?.javaScriptCanOpenWindowsAutomatically = true
        webView?.setDownloadListener { url: String?, _: String?, _: String?, _: String?, _: Long ->
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            activity.startActivity(intent)
        }
        return settings
    }

    private fun getPhoneAndroidSDK(): Int {
        var version = 0
        try {
            version = Integer.valueOf(Build.VERSION.SDK)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return version
    }

    fun loadUrl(webView: WebView?,url: String?){
        if (url == null || !url.startsWith("http"))
            webViewImpl?.showError()
        else webView?.loadUrl(url)
    }

    fun loadDataWithBaseURL(webView: WebView?,content: String?){
        if (TextUtils.isEmpty(content))
            webViewImpl?.showError()
        else {
            val sb = StringBuilder()
            sb.append(getHtmlData(content))
            webView?.loadDataWithBaseURL("about:blank", sb.toString(), "text/html", "utf-8", null as String?)
        }
    }

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

}

private class MyWebViewClient(private var impl:BaseImpl?) : WebViewClient(){
    private var loadError = false

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (url == null)  return true
        chooseMode = if (url.contains("vedio")) PictureMimeType.ofVideo() else PictureMimeType.ofImage()
        if (url.trim().startsWith("tel")) { //特殊情况tel，调用系统的拨号软件拨号【<a href="tel:1111111111">1111111111</a>】
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            view?.context?.startActivity(intent)
        }
        return super.shouldOverrideUrlLoading(view, url)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (loadError)
            impl?.showError()
        else
            impl?.loadSuc()
    }


    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        loadError = false
    }

    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        super.onReceivedError(view, request, error)
        loadError = true
    }
}


private class MyWebChromeClient(private val mLayout: FrameLayout?,private val activity: Activity) : WebChromeClient() {
    private var mCustomViewCallback: CustomViewCallback? = null

    //  横屏时，显示视频的view
    private var mCustomView: View? = null
    override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {
        callback.invoke(origin, true, true)
    }

    fun openFileChooser(valueCallback: ValueCallback<Uri?>?) {
        uploadMessage = valueCallback
        openImageChooserActivity()
    }

    // For Android  >= 3.0
    fun openFileChooser(valueCallback: ValueCallback<Uri?>?, acceptType: String?) {
        uploadMessage = valueCallback
        openImageChooserActivity()
    }

    //For Android  >= 4.1
    fun openFileChooser(valueCallback: ValueCallback<Uri?>?, acceptType: String?, capture: String?) {
        uploadMessage = valueCallback
        openImageChooserActivity()
    }

    // For Android >= 5.0
    override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: FileChooserParams): Boolean {
        uploadMessageAboveL = filePathCallback
        openImageChooserActivity()
        return true
    }

    private fun openImageChooserActivity() {
        selectImg(activity,0)
    }


    override fun onShowCustomView(view: View, callback: CustomViewCallback) {
        super.onShowCustomView(view, callback)
        //如果view 已经存在，则隐藏
        if (mCustomView != null) {
            callback.onCustomViewHidden()
            return
        }
        mCustomView = view
        mCustomView?.visibility = View.VISIBLE
        mCustomViewCallback = callback
        mLayout?.addView(mCustomView)
        mLayout?.visibility = View.VISIBLE
        mLayout?.bringToFront()
    }

    override fun onHideCustomView() {
        super.onHideCustomView()
        if (mCustomView == null) return
        mCustomView?.visibility = View.GONE
        mLayout?.removeView(mCustomView)
        mCustomView = null
        mLayout?.visibility = View.GONE
        mCustomViewCallback?.onCustomViewHidden()
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //竖屏
    }

}