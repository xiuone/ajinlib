package compress.zibin.luban

import java.io.File

interface OnCompressListener {
    /**
     * Fired when a compression returns successfully, override to handle in your own code
     */
    fun onSuccess(source: String?, compressFile: File?)

    /**
     * Fired when a compression fails to complete, override to handle in your own code
     */
    fun onError(source: String?, e: Throwable?)
}