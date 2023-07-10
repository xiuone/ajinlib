package com.xy.picture.select.crop


class ImageCropEngineBack : ImageCropEngineBase() {
    override fun isCircle(): Boolean  = false
    override fun withAspectRatioX(): Float = 3F
    override fun withAspectRatioY(): Float = 2F
}