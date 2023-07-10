package com.zibin.luban.io

import android.content.ContentResolver
import android.net.Uri
import com.zibin.luban.io.BufferedInputStreamWrap
import com.zibin.luban.io.ArrayPoolProvide
import com.zibin.luban.io.PoolAble
import java.io.Closeable
import java.io.FileInputStream
import java.io.InputStream
import java.lang.Exception
import java.util.HashSet
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.Volatile
import kotlin.jvm.Synchronized
import kotlin.Throws
import kotlin.jvm.JvmOverloads

/**
 * @author：luck
 * @date：2021/8/26 4:07 下午
 * @describe：ArrayPoolProvide
 */
class ArrayPoolProvide {
    /**
     * Uri对应的BufferedInputStreamWrap缓存Key
     */
    private val keyCache = HashSet<String>()

    /**
     * Uri对应的BufferedInputStreamWrap缓存数据
     */
    private val bufferedLruCache = ConcurrentHashMap<String, BufferedInputStreamWrap>()

    /**
     * byte[]数组的缓存队列
     */
    private val arrayPool = LruArrayPool(LruArrayPool.Companion.DEFAULT_SIZE)

    /**
     * 获取相应的byte数组
     *
     * @param bufferSize
     */
    operator fun get(bufferSize: Int): ByteArray? {
        return arrayPool.get(bufferSize, ByteArray::class.java)
    }

    /**
     * 缓存相应的byte数组
     *
     * @param buffer
     */
    fun put(buffer: ByteArray?) {
        arrayPool.put(buffer)
    }

    /**
     * ContentResolver openInputStream
     *
     * @param resolver ContentResolver
     * @param uri      data
     * @return
     */
    fun openInputStream(resolver: ContentResolver, uri: Uri): InputStream? {
        var bufferedInputStreamWrap: BufferedInputStreamWrap?
        try {
            bufferedInputStreamWrap = bufferedLruCache[uri.toString()]
            if (bufferedInputStreamWrap != null) {
                bufferedInputStreamWrap.reset()
            } else {
                bufferedInputStreamWrap = wrapInputStream(resolver, uri)
            }
        } catch (e: Exception) {
            bufferedInputStreamWrap = try {
                return resolver.openInputStream(uri)
            } catch (exception: Exception) {
                exception.printStackTrace()
                wrapInputStream(resolver, uri)
            }
        }
        return bufferedInputStreamWrap
    }

    /**
     * open real path FileInputStream
     *
     * @param path data
     * @return
     */
    fun openInputStream(path: String): InputStream? {
        var bufferedInputStreamWrap: BufferedInputStreamWrap?
        try {
            bufferedInputStreamWrap = bufferedLruCache[path]
            if (bufferedInputStreamWrap != null) {
                bufferedInputStreamWrap.reset()
            } else {
                bufferedInputStreamWrap = wrapInputStream(path)
            }
        } catch (e: Exception) {
            bufferedInputStreamWrap = wrapInputStream(path)
        }
        return bufferedInputStreamWrap
    }

    /**
     * BufferedInputStreamWrap
     *
     * @param resolver ContentResolver
     * @param uri      data
     */
    private fun wrapInputStream(resolver: ContentResolver, uri: Uri): BufferedInputStreamWrap? {
        var bufferedInputStreamWrap: BufferedInputStreamWrap? = null
        try {
            bufferedInputStreamWrap = BufferedInputStreamWrap(resolver.openInputStream(uri))
            val available = bufferedInputStreamWrap.available()
            bufferedInputStreamWrap.mark(if (available > 0) available else BufferedInputStreamWrap.Companion.DEFAULT_MARK_READ_LIMIT)
            bufferedLruCache[uri.toString()] = bufferedInputStreamWrap
            keyCache.add(uri.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bufferedInputStreamWrap
    }

    /**
     * BufferedInputStreamWrap
     *
     * @param path data
     */
    private fun wrapInputStream(path: String): BufferedInputStreamWrap? {
        var bufferedInputStreamWrap: BufferedInputStreamWrap? = null
        try {
            bufferedInputStreamWrap = BufferedInputStreamWrap(FileInputStream(path))
            val available = bufferedInputStreamWrap.available()
            bufferedInputStreamWrap.mark(if (available > 0) available else BufferedInputStreamWrap.Companion.DEFAULT_MARK_READ_LIMIT)
            bufferedLruCache[path] = bufferedInputStreamWrap
            keyCache.add(path)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bufferedInputStreamWrap
    }

    /**
     * 清空内存占用
     */
    fun clearMemory() {
        for (key in keyCache) {
            val inputStreamWrap = bufferedLruCache[key]
            close(inputStreamWrap)
            bufferedLruCache.remove(key)
        }
        keyCache.clear()
        arrayPool.clearMemory()
    }

    companion object {
        private var mInstance: ArrayPoolProvide? = null
        @JvmStatic
        val instance: ArrayPoolProvide?
            get() {
                if (mInstance == null) {
                    synchronized(ArrayPoolProvide::class.java) {
                        if (mInstance == null) {
                            mInstance = ArrayPoolProvide()
                        }
                    }
                }
                return mInstance
            }

        fun close(c: Closeable?) {
            // java.lang.IncompatibleClassChangeError: interface not implemented
            if (c is Closeable) {
                try {
                    c.close()
                } catch (e: Exception) {
                    // silence
                }
            }
        }
    }
}