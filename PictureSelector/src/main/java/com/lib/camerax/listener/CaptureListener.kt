package com.lib.camerax.listener

import com.lib.camerax.listener.CameraXOrientationEventListener.OnOrientationChangedListener
import com.lib.camerax.listener.CameraXPreviewViewTouchListener.CustomTouchListener

/**
 * @author：luck
 * @date：2020-01-04 13:38
 * @describe：CaptureListener
 */
interface CaptureListener {
    fun takePictures()
    fun recordShort(time: Long)
    fun recordStart()
    fun recordEnd(time: Long)
    fun changeTime(duration: Long)
    fun recordZoom(zoom: Float)
    fun recordError()
}