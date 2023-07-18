package camerax.luck.lib.camerax.listener

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