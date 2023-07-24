package camerax.luck.lib.camerax.widget.camera.callback

import android.graphics.Point
import androidx.camera.core.FocusMeteringAction
import camerax.luck.lib.camerax.widget.camera.CustomCameraViewListener
import camerax.luck.lib.camerax.widget.camera.PreviewViewTouchListener
import java.util.concurrent.TimeUnit

class CallBackCustomTouch(private val listener: CustomCameraViewListener?):
    PreviewViewTouchListener.CustomTouchListener {
    override fun zoom(delta: Float) {
        val value = listener?.onCreateCameraInfo()?.zoomState?.value
        if (value != null) {
            val currentZoomRatio = value.zoomRatio
            listener?.onCreateCameraControl()?.setZoomRatio(currentZoomRatio * delta)
        }
    }

    override fun click(x: Float, y: Float) {
        val previewView = listener?.onCreatePreviewView()
        val cameraInfo = listener?.onCreateCameraInfo()
        val factory = previewView?.meteringPointFactory?:return
        val point = factory.createPoint(x, y)
        val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
            .setAutoCancelDuration(3, TimeUnit.SECONDS)
            .build()
        if (cameraInfo?.isFocusMeteringSupported(action) == true) {
            listener?.onCreateCameraControl()?.cancelFocusAndMetering()
            listener?.onCreateFocusImageView()?.isDisappear = false
            listener?.onCreateFocusImageView()?.startFocus(Point(x.toInt(), y.toInt()))
            val future = listener?.onCreateCameraControl()?.startFocusAndMetering(action)
            future?.addListener({
                try {
                    val result = future.get()
                    listener?.onCreateFocusImageView()?.isDisappear = true
                    if (result.isFocusSuccessful) {
                        listener?.onCreateFocusImageView()?.onFocusSuccess()
                    } else {
                        listener?.onCreateFocusImageView()?.onFocusFailed()
                    }
                } catch (ignored: Exception) {
                }
            }, listener?.onCreateExecutor())
        }
    }

    override fun doubleClick(x: Float, y: Float) {
        val value = listener?.onCreateCameraInfo()?.zoomState?.value
        if (value != null) {
            val currentZoomRatio = value.zoomRatio
            val minZoomRatio = value.minZoomRatio
            if (currentZoomRatio > minZoomRatio) {
                listener?.onCreateCameraControl()?.setLinearZoom(0f)
            } else {
                listener?.onCreateCameraControl()?.setLinearZoom(0.5f)
            }
        }
    }
}