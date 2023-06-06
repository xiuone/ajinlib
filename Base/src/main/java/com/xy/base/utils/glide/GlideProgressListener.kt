package com.xy.base.utils.glide

interface GlideProgressListener {
    fun onProgress(url: String?,progress:Int, bytesRead: Long, totalBytes: Long)
}