package com.jianbian.baselib.http

import com.alibaba.fastjson.JSON
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit


object HttpUtils {
    private var client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)//设置连接超时时间
        .readTimeout(60, TimeUnit.SECONDS)//设置读取超时时间
        .build()

    fun postJson(url: String, any: Any?): Request.Builder {
        val body: RequestBody = JSON.toJSONString(any).toRequestBody(MyMediaType.JSON_TYPE)
        return Request.Builder()
            .url(url)
            .post(body)
    }

    fun addHead(builder: Request.Builder, data: HashMap<String, String?>?): Request {
        if (data != null) {
            for (key in data.keys) {
                var value = data[key]
                value?.run {
                    builder.addHeader(key, this)
                }
            }
        }
        return builder.build()
    }

    fun call(request: Request, callBack: Callback): Call {
        var call = client.newCall(request)
        call.enqueue(callBack)
        return call
    }

    fun get(url: String, data: HashMap<String, String?>?): Request.Builder {
        var getUrl = url
        if (data != null) {
            for (key in data.keys) {
                var value = data[key]
                if (getUrl != null && value != null) {
                    getUrl = if (getUrl.indexOf("?") != -1)
                        "$getUrl&$key=$value"
                    else "$getUrl?$key=$value"
                }
            }
        }
        return Request.Builder()
            .url(getUrl)
            .get()
    }
}
