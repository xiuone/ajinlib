package com.jianbian.baselib.http.callback

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.util.IOUtils
import com.jianbian.baselib.utils.NetworkUtils
import com.jianbian.baselib.utils.ToastUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.json.JSONObject
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.nio.charset.Charset

abstract class  DataCallBack<M>(val context: Context?,val isOpenAct: Boolean) : Callback {

    override fun onFailure(call: Call, e: IOException) {
        logHttp(call.request(),e.toString())
        if (context != null){
            if (!NetworkUtils.isNetworkConnected(context)) {
                    ToastUtils.show(context,"网络错误")
            }else{
                ToastUtils.show(context,e.message)
            }
        }
        Handler(Looper.getMainLooper()).post {
            onError()
        }
    }

    override fun onResponse(call: Call, response: Response) {
        try {
            val str = response.body?.string()
            if (str == null) {
                onFailure(call, IOException("数据异常,请稍后再试"))
                return
            }
            val dataObj = JSONObject(str)
            val code = dataObj.getInt("code")
            val message = dataObj.getString("message")
            when (code) {
                0 -> {
                    if (!dataObj.isNull("data")){
                        var data = dataObj.getString("data")
                        val desData = data
                        if (!TextUtils.isEmpty(desData))
                            data = desData
                        val type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
                        val obj: M = JSON.parseObject(data,type)
                        logHttp(call.request(),data)
                        Handler(Looper.getMainLooper()).post {
                            onSuc(obj)
                        }
                    }else{
                        logHttp(call.request(),"没有data数据")
                        Handler(Looper.getMainLooper()).post {
                            onSuc(null)
                        }
                    }
                }
                1->{
                    if (isOpenAct){

                    }
                    onFailure(call,IOException(message))
                }
                else -> {
                    onFailure(call,IOException(message))
                }
            }
        }catch (e:Exception){
            Log.e("==``",e.toString())
            onFailure(call,IOException("数据异常，请稍后再试"))
        }
    }


    abstract fun onSuc(data :M?)

    open fun onError(){

    }

    private fun logHttp(request: Request, retrunStr: String?) {
        val requestBody = request.body
        var reqBody: String? = null
        if (requestBody != null) {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            var charset: Charset? = IOUtils.UTF8
            val contentType = requestBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(IOUtils.UTF8)
            }
            reqBody = buffer.readString(charset!!)
        }

        Log.i(
            "==``", "\nmethod：${request.method}\n" +
                    "url：${request.url}\n" +
                    "headers: ${request.headers.toString()}\n" +
                    "body：${reqBody.toString()}"
        )
        Log.i("==``", "\nreturn:$retrunStr")
    }
}