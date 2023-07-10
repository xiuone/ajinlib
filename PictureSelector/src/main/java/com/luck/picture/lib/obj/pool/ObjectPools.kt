package com.luck.picture.lib.obj.pool

import java.util.*

/**
 * @author：luck
 * @date：2022/6/25 22:36 晚上
 * @describe：ObjectPools
 */
class ObjectPools {
    interface Pool<T> {
        /**
         * 获取对象
         */
        fun acquire(): T

        /**
         * 释放对象
         */
        fun release(obj: T): Boolean

        /**
         * 销毁对象池
         */
        fun destroy()
    }

    open class SimpleObjectPool<T> : Pool<T> {
        private val mPool: LinkedList<T>
        override fun acquire(): T {
            return mPool.poll()
        }

        override fun release(obj: T): Boolean {
            return if (isInPool(obj)) {
                false
            } else mPool.add(obj)
        }

        override fun destroy() {
            mPool.clear()
        }

        private fun isInPool(obj: T): Boolean {
            return mPool.contains(obj)
        }

        init {
            mPool = LinkedList()
        }
    }

    class SynchronizedPool<T> : SimpleObjectPool<T>() {
        private val mLock = Any()
        override fun acquire(): T {
            synchronized(mLock) { return super.acquire() }
        }

        override fun release(obj: T): Boolean {
            synchronized(mLock) { return super.release(obj) }
        }

        override fun destroy() {
            synchronized(mLock) { super.destroy() }
        }
    }
}