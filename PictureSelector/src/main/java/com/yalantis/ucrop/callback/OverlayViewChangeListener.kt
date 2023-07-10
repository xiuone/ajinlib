package com.yalantis.ucrop.callback

import android.graphics.RectF

/**
 * Created by Oleksii Shliama.
 */
interface OverlayViewChangeListener {
    fun onCropRectUpdated(cropRect: RectF?)
    fun postTranslate(deltaX: Float, deltaY: Float)
}