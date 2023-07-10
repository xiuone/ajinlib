package com.zibin.luban

import java.io.File

interface OnNewCompressListener {
    /**
     * Fired when the compression is started, override to handle in your own code
     */
    fun onStart()

    /**
     * Fired when a compression returns successfully, override to handle in your own code
     */
    fun onSuccess(source: String?, compressFile: File?)

    /**
     * Fired when a compression fails to complete, override to handle in your own code
     */
    fun onError(source: String?, e: Throwable?)
}