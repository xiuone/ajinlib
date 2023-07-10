package com.zibin.luban

import java.io.File

interface OnCompressListener {
    /**
     * Fired when the compression is started, override to handle in your own code
     */
    fun onStart()

    /**
     * Fired when a compression returns successfully, override to handle in your own code
     *
     * @param index compression index
     */
    fun onSuccess(index: Int, compressFile: File?)

    /**
     * Fired when a compression fails to complete, override to handle in your own code
     *
     * @param index compression error index
     */
    fun onError(index: Int, e: Throwable?)
}