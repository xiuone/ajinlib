package com.luck.picture.lib.manager

import com.luck.picture.lib.interfaces.OnCallbackListener.onCall
import com.luck.picture.lib.config.SelectMimeType.ofImage
import kotlin.jvm.JvmOverloads

/**
 * @author：luck
 * @date：2021/11/20 8:57 下午
 * @describe：SelectedManager
 */
object SelectedManager {
    const val INVALID = -1
    const val ADD_SUCCESS = 0
    const val REMOVE = 1
    const val SUCCESS = 200
}