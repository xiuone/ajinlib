package com.xy.baselib.net

import android.content.Context
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import com.xy.baselib.exp.Logger
import com.xy.baselib.exp.showToast

abstract class BaseCallBack<T>(private val context: Context,protected val errorStr: String?=null) : StringCallback() {

    override fun onError(response: Response<String>) {
        super.onError(response)
        Logger.e(response.message())
        onError(errorStr)
    }

    override fun onStart(request: Request<String, out Request<Any, Request<*, *>>>?) {
        super.onStart(request)
    }
    override fun onSuccess(response: Response<String?>?) {
        val body = response?.body()?:""
        if (body.isEmpty()){
            onError(errorStr)
            return
        }
        onSuccess(body)
    }
    abstract fun onSuccess(data:String)
    abstract fun checkLogin(data: T):Boolean

    open fun onSuccess(data:T){}
    open fun onError(string: String?){
        context.showToast(string)
    }
}