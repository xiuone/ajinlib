package com.xy.baselib.utils

import android.content.Context
import com.xy.baselib.mvp.impl.DownListener
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import java.io.File

object DownFileUtils {
    fun startDown(context:Context,url:String?,dir:String,downListener: DownListener){
        var dirFile = File(dir)
        if (dirFile.exists() && !dirFile.isDirectory){
            dirFile.mkdir()
        } else if (!dirFile.exists()){
            dirFile.mkdir()
        }
        if (!dirFile.exists()){
            ToastUtils.show(context,"文件夹不存在")
            return
        }
        if (url == null || !url.startsWith("http")){
            ToastUtils.show(context,"下载链接错误")
            return
        }
        val urlEndIndex = url.lastIndexOf("/") + 1
        val filename = url.substring(urlEndIndex, url.length)
        OkGo.get<File>(url).tag(this).execute(object : FileCallback(dir,   filename) {
            override fun onSuccess(response: Response<File?>) {
                if (response.body() == null || response.body()?.exists() != true) {
                    onError(response)
                    return
                }
                downListener.onSuccess(response.body()!!)
            }

            override fun downloadProgress(progress: Progress?) {
                super.downloadProgress(progress)
                downListener.downloadProgress(progress?.fraction?:0F)
            }

            override fun onStart(request: com.lzy.okgo.request.base.Request<File, out com.lzy.okgo.request.base.Request<Any, com.lzy.okgo.request.base.Request<*, *>>>?) {
                super.onStart(request)
                downListener.downloadProgress(0F)
            }

            override fun onError(response: Response<File?>) {
                downListener.onError("下载错误")
            }
        })
    }

    fun stopDown(){
        OkGo.getInstance().cancelTag(this)
    }
}