package com.xy.baselib.net

import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.Callback
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import com.lzy.okgo.model.HttpHeaders
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.PostRequest
import com.lzy.okgo.request.base.Request
import java.io.File


fun init(debug: Boolean) {
    if (!debug) {
        var interceptors = OkGo.getInstance().okHttpClient.interceptors()
        if (interceptors == null) interceptors = ArrayList()
        for (interceptor in interceptors) {
            if (interceptor is HttpLoggingInterceptor)
                interceptor.setPrintLevel(HttpLoggingInterceptor.Level.NONE)
        }
    }
}

fun String.post( callback: Callback<String>?) {
    post( null, callback)
}

fun String.post(hashMap: HashMap<String, String>?, callback: Callback<String>?) {
    post( this, hashMap, callback)
}

fun String.post(hashMap: HashMap<String, String>?, headMap: HashMap<String, String>?, callback: Callback<String>?) {
    post( this, hashMap, headMap, callback)
}


fun String.post(tag: String?, hashMap: HashMap<String, String>?, callback: Callback<String>?) {
    post(tag, hashMap, null, callback)
}


fun String.post(tag: String?, paramsMap: HashMap<String, String>?, headMap: HashMap<String, String>?,
                callback: Callback<String>?) {
    post(tag, paramsMap, headMap, null, callback)
}

fun String.post(tag: String?, paramsMap: HashMap<String, String>?, headMap: HashMap<String, String>?,
                fileMap: HashMap<String, List<File>>?, callback: Callback<String>?) {
    OkGo.getInstance().cancelTag(tag)
    val request = OkGo.post<String>(this)
    request.tag(this).addFile(fileMap).addHead(headMap).addParams(paramsMap)
    request.execute(callback)
}

fun String.postJson(jsonData: String?, headMap: HashMap<String, String>?, callback:Callback<String>){
    postJson(this,jsonData,headMap, callback)
}

fun String.postJson(tag:String,jsonData: String?, headMap: HashMap<String, String>?, callback:Callback<String>){
    OkGo.getInstance().cancelTag(tag)
    val request = OkGo.post<String>(this)
    request.tag(this).upJson(jsonData).addHead(headMap)
    request.execute(callback)
}








fun String.get(callback: Callback<String>?) {
    get(null,callback)
}

fun String.get(hashMap: HashMap<String, String>?, callback: Callback<String>?) {
    get(hashMap,null,callback)
}

fun String.get(hashMap: HashMap<String, String>?, headMap: HashMap<String, String>?,
               callback: Callback<String>?) {
    get(this,hashMap,headMap,callback)
}

fun String.get(tag: String?, paramsMap: HashMap<String, String>?, headMap: HashMap<String, String>?,
               callback: Callback<String>?) {
    OkGo.getInstance().cancelTag(tag)
    val request = OkGo.get<String>(this)
    request.tag(tag).addHead(headMap).addParams(paramsMap)
    request.execute(callback)
}


fun String.down(tag: String?, callback: FileCallback){
    down(tag, null, callback)
}

fun String.down(tag: String?, paramsMap: HashMap<String, String>?, callback: FileCallback){
    down(tag, paramsMap,null, callback)
}

fun String.down(tag: String?, paramsMap: HashMap<String, String>?, headMap: HashMap<String, String>?,
                callback: FileCallback){
    val requestFile = OkGo.get<File>(this)
    requestFile.tag(tag).addHead(headMap).addParams(paramsMap)
    requestFile.execute(callback)
}


private fun PostRequest<*>.addFile(fileMap: HashMap<String, List<File>>?) :PostRequest<*>{
    fileMap?:return this
    for (key in fileMap.keys) {
        addFileParams(key, fileMap[key])
    }
    return this
}

private fun Request<*, *>.addHead(headMap:HashMap<String, String>?) :Request<*, *>{
    headMap?:return this
    val headers = HttpHeaders()
    for (key in headMap.keys) {
        headers.put(key, headMap[key])
    }
    headers(headers)
    return this
}

private fun Request<*, *>.addParams(paramMap: HashMap<String, String>?) {
    if (paramMap != null) {
        for (key in paramMap.keys) {
            params(key, paramMap[key])
        }
    }
}
