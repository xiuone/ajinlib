package com.lib.camerax.listener

import com.lib.camerax.listener.CameraXOrientationEventListener.OnOrientationChangedListener
import com.lib.camerax.listener.CameraXPreviewViewTouchListener.CustomTouchListener

/**
 * @author：luck
 * @date：2020-01-04 13:38
 * @describe：TypeListener
 */
interface TypeListener {
    fun cancel()
    fun confirm()
}