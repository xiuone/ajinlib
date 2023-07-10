package com.zibin.luban

import com.zibin.luban.io.ArrayPoolProvide.Companion.instance
import com.zibin.luban.io.ArrayPoolProvide.clearMemory
import com.zibin.luban.io.ArrayPoolProvide.openInputStream
import com.zibin.luban.InputStreamProvider
import kotlin.Throws
import com.zibin.luban.io.ArrayPoolProvide
import com.zibin.luban.OnRenameListener
import com.zibin.luban.OnCompressListener
import com.zibin.luban.OnNewCompressListener
import com.zibin.luban.CompressionPredicate
import com.zibin.luban.Luban
import com.zibin.luban.LubanUtils
import java.io.IOException
import java.io.InputStream
import kotlin.jvm.JvmOverloads

/**
 * Automatically close the previous InputStream when opening a new InputStream,
 * and finally need to manually call [.close] to release the resource.
 */
abstract class InputStreamAdapter : InputStreamProvider {
    @Throws(IOException::class)
    override fun open(): InputStream? {
        return openInternal()
    }

    @Throws(IOException::class)
    abstract fun openInternal(): InputStream?
    override fun close() {
        instance!!.clearMemory()
    }
}