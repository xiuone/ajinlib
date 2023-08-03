package xy.xy.base.assembly.picture.select.camera

import androidx.fragment.app.Fragment
import com.luck.lib.camerax.SimpleCameraX
import com.luck.picture.lib.interfaces.OnCameraInterceptListener

class ImageCameraIntercept : OnCameraInterceptListener {


    override fun openCamera(fragment: Fragment?, cameraMode: Int, requestCode: Int) {
        val fragment = fragment?:return
        val context = fragment.context?:return
        val camera = SimpleCameraX.of()
        camera.setCameraMode(cameraMode)
        camera.setVideoFrameRate(25)
        camera.setVideoBitRate(3 * 1024 * 1024)
        camera.isDisplayRecordChangeTime(true)
        camera.start(fragment.requireActivity(), fragment, requestCode)
    }
}