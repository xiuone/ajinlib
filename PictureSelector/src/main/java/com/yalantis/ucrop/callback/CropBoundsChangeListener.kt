package com.yalantis.ucrop.callback

/**
 * Interface for crop bound change notifying.
 */
interface CropBoundsChangeListener {
    fun onCropAspectRatioChanged(cropRatio: Float)
}