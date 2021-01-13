package com.jianbian.baselib.utils.gilde

interface OnProgressListener {
    fun onProgress(isComplete: Boolean, percentage: Int, bytesRead: Long, totalBytes: Long)
}