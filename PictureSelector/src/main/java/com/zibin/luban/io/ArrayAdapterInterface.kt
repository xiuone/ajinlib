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
 * @date：2021/8/26 3:19 下午
 * @describe：ArrayAdapterInterface
 */
internal interface ArrayAdapterInterface<T> {
    /**
     * TAG for logging.
     */
    val tag: String

    /**
     * Return the length of the given array.
     */
    fun getArrayLength(array: T): Int

    /**
     * Allocate and return an array of the specified size.
     */
    fun newArray(length: Int): T

    /**
     * Return the size of an element in the array in bytes (e.g. for int return 4).
     */
    val elementSizeInBytes: Int
}