package com.luck.picture.lib.config

import com.luck.picture.lib.config.SelectorConfig
import kotlin.jvm.Synchronized
import com.luck.picture.lib.utils.FileDirMap
import kotlin.jvm.Volatile
import com.luck.picture.lib.config.SelectorProviders

/**
 * @author：luck
 * @date：2022/6/3 1:49 下午
 * @describe：文件大小单位
 */
object FileSizeUnit {
    const val KB: Long = 1024
    const val MB = (1024 * 1024).toLong()
    const val GB = (1024 * 1024 * 1024).toLong()
    const val ACCURATE_GB = 1000 * 1000 * 1000
    const val ACCURATE_MB = 1000 * 1000
    const val ACCURATE_KB = 1000
}