package com.zibin.luban.io

import com.zibin.luban.io.BufferedInputStreamWrap
import com.zibin.luban.io.ArrayPoolProvide
import com.zibin.luban.io.PoolAble
import kotlin.jvm.Volatile
import kotlin.jvm.Synchronized
import kotlin.Throws
import kotlin.jvm.JvmOverloads

/**
 * @author：luck
 * @date：2021/8/26 3:20 下午
 * @describe：ByteArrayAdapter
 */
class ByteArrayAdapter : ArrayAdapterInterface<ByteArray> {
    override fun getArrayLength(array: ByteArray): Int {
        return array.size
    }

    override fun newArray(length: Int): ByteArray {
        return ByteArray(length)
    }

    override val elementSizeInBytes: Int
        get() = 1

    companion object {
        val tag = "ByteArrayPool"
            get() = Companion.field
    }
}