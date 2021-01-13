package com.xy.baselib.utils.gilde

import android.text.TextUtils
import okhttp3.OkHttpClient
import java.util.*

object ProgressManager {
    private val listenersMap = Collections.synchronizedMap(HashMap<String, OnProgressListener>())
    private var okHttpClient: OkHttpClient? = null

    fun getOkHttpClient(): OkHttpClient {
        if (okHttpClient == null) {
            okHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
                    val builder = chain.request().newBuilder()
                    builder.addHeader("Accept-Encoding", "identity") //强迫服务器不走压缩
                    chain.proceed(builder.build())
                }.addNetworkInterceptor { chain ->
                    val request = chain.request()
                    val response = chain.proceed(request)
                    response.newBuilder()
                        .body(ProgressResponseBody(request.url().toString(), LISTENER, response.body()!!))
                        .build()
                }
                .build()
        }
        return okHttpClient!!
    }

    private val LISTENER: ProgressResponseBody.InternalProgressListener =
        object : ProgressResponseBody.InternalProgressListener {
            override fun onProgress(url: String?, bytesRead: Long, totalBytes: Long) {
                val onProgressListener = getProgressListener(url)
                val percentage = (bytesRead * 1f / totalBytes * 100f).toInt()
                val isComplete = percentage >= 100
                onProgressListener?.onProgress(isComplete, percentage, bytesRead, totalBytes)
                if (isComplete) {
                    removeListener(url)
                }
            }
        }

    fun addListener(url: Any?, listener: OnProgressListener?) {
        if (url != null && url is String && listener != null) {
            listenersMap[url] = listener
        }
        listener?.onProgress(true, 1, 0, 0)
    }

    fun removeListener(url: String?) {
        if (!TextUtils.isEmpty(url)) {
            listenersMap.remove(url)
        }
    }

    fun getProgressListener(url: String?): OnProgressListener? {
        return if (TextUtils.isEmpty(url) || listenersMap == null || listenersMap.isEmpty()) {
            null
        } else listenersMap[url]
    }
}