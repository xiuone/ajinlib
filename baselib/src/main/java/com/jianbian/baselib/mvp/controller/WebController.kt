package com.jianbian.baselib.mvp.controller

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.webkit.*
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.jianbian.baselib.mvp.impl.BaseImpl

private const val FILE_CHOOSER_RESULT_CODE = 10000
private var uploadMessage: ValueCallback<Uri?>? = null
private var uploadMessageAboveL: ValueCallback<Array<Uri>>? = null

class WebController(private var webViewImpl: BaseImpl?) {
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FILE_CHOOSER_RESULT_CODE) { //处理返回的图片，并进行上传
            if (null == uploadMessage && null == uploadMessageAboveL) return
            val result = if (data == null || resultCode != Activity.RESULT_OK) null else data.data
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data)
            } else if (uploadMessage != null) {
                uploadMessage!!.onReceiveValue(result)
                uploadMessage = null
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun onActivityResultAboveL(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null) return
        var results: Array<Uri> = emptyArray()
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                val dataString = intent.dataString
                val clipData = intent.clipData
                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        val item = clipData.getItemAt(i)
                        results[i] = item.uri
                    }
                }
                if (dataString != null) results =
                    arrayOf(Uri.parse(dataString))
            }
        }
        uploadMessageAboveL?.onReceiveValue(results)
        uploadMessageAboveL = null
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun setWebView(webView: WebView, mLayout: FrameLayout, activity:Activity) {
        webView.webChromeClient = MyWebChromeClient(mLayout,activity)
        webView.webViewClient = MyWebViewClient(webViewImpl)
        webView.overScrollMode = WebView.OVER_SCROLL_NEVER
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        val settings: WebSettings = webView.settings
        if (getPhoneAndroidSDK() >= 14) { // 4.0需打开硬件加速
            activity.getWindow().setFlags(0x1000000, 0x1000000)
        }
        //	settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//加上这句有的手机无法播放优酷视频
        settings.javaScriptEnabled = true
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.domStorageEnabled = true
        settings.setGeolocationEnabled(true)
        webView.isVerticalScrollBarEnabled = false // 取消Vertical ScrollBar显示
        webView.isHorizontalScrollBarEnabled = false
        settings.allowFileAccessFromFileURLs = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        webView.setDownloadListener { url: String?, _: String?, _: String?, _: String?, _: Long ->
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            activity.startActivity(intent)
        }
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

    fun loadUrl(webView: WebView,url: String?){
        if (TextUtils.isEmpty(url))
            webViewImpl?.showError()
        else webView.loadUrl(url!!)
    }

    fun loadDataWithBaseURL(webView: WebView,content: String?){
        if (TextUtils.isEmpty(content))
            webViewImpl?.showError()
        else {
            val sb = StringBuilder()
            sb.append(getHtmlData(content))
            webView.loadDataWithBaseURL("about:blank", sb.toString(), "text/html", "utf-8", null as String?)
        }
    }

    private fun getHtmlData(bodyHTML: String?): String? {
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
        return "$head$bodyHTML</body>\n</html>"
    }


}

private class MyWebViewClient(private var impl:BaseImpl?) : WebViewClient(){
    private var loadError = false
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return super.shouldOverrideUrlLoading(view, request)
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


private class MyWebChromeClient(private val mLayout: FrameLayout,private val activity: Activity) : WebChromeClient() {
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
        //调用自己的图库
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        activity.startActivityForResult(
            Intent.createChooser(intent, "Image Chooser"),
           FILE_CHOOSER_RESULT_CODE
        )
    }

    override fun onShowCustomView(view: View, callback: CustomViewCallback) {
        super.onShowCustomView(view, callback)
        //如果view 已经存在，则隐藏
        if (mCustomView != null) {
            callback.onCustomViewHidden()
            return
        }
        mCustomView = view
        mCustomView!!.visibility = View.VISIBLE
        mCustomViewCallback = callback
        mLayout.addView(mCustomView)
        mLayout.visibility = View.VISIBLE
        mLayout.bringToFront()
    }

    override fun onHideCustomView() {
        super.onHideCustomView()
        if (mCustomView == null) {
            return
        }
        mCustomView!!.visibility = View.GONE
        mLayout.removeView(mCustomView)
        mCustomView = null
        mLayout.visibility = View.GONE
        try {
            mCustomViewCallback!!.onCustomViewHidden()
        } catch (e: Exception) {
        }
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //竖屏
    }

}