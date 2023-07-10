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
 * @date：2021/8/26 3:21 下午
 * @describe：IntegerArrayAdapter
 */
class IntegerArrayAdapter : ArrayAdapterInterface<IntArray> {
    override fun getArrayLength(array: IntArray): Int {
        return array.size
    }

    override fun newArray(length: Int): IntArray {
        return IntArray(length)
    }

    override val elementSizeInBytes: Int
        get() = 4

    companion object {
        val tag = "IntegerArrayPool"
            get() = Companion.field
    }
}