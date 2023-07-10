package com.luck.picture.lib.config

import com.luck.picture.lib.config.SelectorConfig
import kotlin.jvm.Synchronized
import com.luck.picture.lib.utils.FileDirMap
import kotlin.jvm.Volatile
import com.luck.picture.lib.config.SelectorProviders

/**
 * @author：luck
 * @date：2022/3/25 1:41 下午
 * @describe：PermissionEvent
 */
object PermissionEvent {
    const val EVENT_SOURCE_DATA = -1
    const val EVENT_SYSTEM_SOURCE_DATA = -2
    val EVENT_IMAGE_CAMERA = SelectMimeType.ofImage()
    val EVENT_VIDEO_CAMERA = SelectMimeType.ofVideo()
}