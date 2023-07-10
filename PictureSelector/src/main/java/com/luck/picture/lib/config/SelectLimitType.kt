package com.luck.picture.lib.config

import com.luck.picture.lib.config.SelectorConfig
import kotlin.jvm.Synchronized
import com.luck.picture.lib.utils.FileDirMap
import kotlin.jvm.Volatile
import com.luck.picture.lib.config.SelectorProviders

/**
 * @author：luck
 * @date：2022/1/8 2:25 下午
 * @describe：SelectLimitType
 */
object SelectLimitType {
    const val SELECT_MAX_FILE_SIZE_LIMIT = 1
    const val SELECT_MIN_FILE_SIZE_LIMIT = 2
    const val SELECT_NOT_WITH_SELECT_LIMIT = 3
    const val SELECT_MAX_SELECT_LIMIT = 4
    const val SELECT_MIN_SELECT_LIMIT = 5
    const val SELECT_MAX_VIDEO_SELECT_LIMIT = 6
    const val SELECT_MIN_VIDEO_SELECT_LIMIT = 7
    const val SELECT_MAX_VIDEO_SECOND_SELECT_LIMIT = 8
    const val SELECT_MIN_VIDEO_SECOND_SELECT_LIMIT = 9
    const val SELECT_MAX_AUDIO_SECOND_SELECT_LIMIT = 10
    const val SELECT_MIN_AUDIO_SECOND_SELECT_LIMIT = 11
    const val SELECT_MIN_AUDIO_SELECT_LIMIT = 12
    const val SELECT_NOT_SUPPORT_SELECT_LIMIT = 13
}