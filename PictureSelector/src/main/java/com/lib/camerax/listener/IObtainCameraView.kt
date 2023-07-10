package com.lib.camerax.listener

import android.view.ViewGroup
import com.lib.camerax.listener.CameraXOrientationEventListener.OnOrientationChangedListener
import com.lib.camerax.listener.CameraXPreviewViewTouchListener.CustomTouchListener

/**
 * @author：luck
 * @date：2022/3/15 12:18 下午
 * @describe：IObtainCameraView
 */
interface IObtainCameraView {
    val customCameraView: ViewGroup?
}