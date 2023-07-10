package com.zibin.luban.io

import com.zibin.luban.io.BufferedInputStreamWrap
import com.zibin.luban.io.ArrayPoolProvide
import com.zibin.luban.io.PoolAble
import java.util.*
import kotlin.jvm.Volatile
import kotlin.jvm.Synchronized
import kotlin.Throws
import kotlin.jvm.JvmOverloads

/**
 * @author：luck
 * @date：2021/8/26 3:13 下午
 * @describe：BaseKeyPool
 */
internal abstract class BaseKeyPool<T : PoolAble?> {
    private val keyPool = createQueue<T>(MAX_SIZE)
    fun get(): T? {
        var result = keyPool.poll()
        if (result == null) {
            result = create()
        }
        return result
    }

    fun offer(key: T) {
        if (keyPool.size < MAX_SIZE) {
            keyPool.offer(key)
        }
    }

    abstract fun create(): T

    companion object {
        private const val MAX_SIZE = 20
        fun <T> createQueue(size: Int): Queue<T> {
            return ArrayDeque(size)
        }
    }
}