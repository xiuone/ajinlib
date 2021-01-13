package com.xy.baselib.mvp.impl

import java.io.File

interface DownListener{
    fun onSuccess(file:File)
    fun downloadProgress(progress:Float)
    fun onError(message:String?)
}