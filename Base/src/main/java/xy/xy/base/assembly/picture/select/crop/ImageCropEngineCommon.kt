package xy.xy.base.assembly.picture.select.crop


class ImageCropEngineCommon : ImageCropEngineBase() {
    override fun isCircle(): Boolean  = false
    override fun withAspectRatioX(): Float = 3F
    override fun withAspectRatioY(): Float = 2F
}