package camerax.luck.lib.camerax.widget.camera.callback

import android.hardware.display.DisplayManager
import camerax.luck.lib.camerax.widget.camera.CustomCameraViewListener

class CallBackCustomDisplay(private val listener: CustomCameraViewListener?) : DisplayManager.DisplayListener {
    override fun onDisplayAdded(displayId: Int) {}
    override fun onDisplayRemoved(displayId: Int) {}
    override fun onDisplayChanged(displayId: Int) {
        if (displayId == listener?.onCreateDisplayId()) {
            val rotation = listener.onCreatePreviewView()?.display?.rotation?:0
            listener.onCreateImageCapture()?.targetRotation = rotation
            listener.onCreateImageAnalysis()?.targetRotation = rotation
        }
    }
}