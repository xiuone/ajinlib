package xy.xy.base.assembly.picture.select.camera

import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import camerax.luck.lib.camerax.SimpleCameraX
import picture.luck.picture.lib.interfaces.OnCameraInterceptListener
import xy.xy.base.R
import xy.xy.base.utils.exp.getResString
import xy.xy.base.utils.exp.getSdImageDir

class ImageCameraIntercept :
    OnCameraInterceptListener {


    override fun openCamera(fragment: Fragment?, cameraMode: Int, requestCode: Int) {
        val fragment = fragment?:return
        val context = fragment.context?:return
        val camera = SimpleCameraX.of()
        camera.isAutoRotation(true)
        camera.setCameraMode(cameraMode)
        camera.setVideoFrameRate(25)
        camera.setVideoBitRate(3 * 1024 * 1024)
        camera.isDisplayRecordChangeTime(true)
        camera.isManualFocusCameraPreview(true)
        camera.isZoomCameraPreview(true)
        camera.setOutputPathDir(context.getSdImageDir(context.getResString(R.string.app_name)))
        camera.setImageEngine { context, url, imageView ->
            Glide.with(context).load(url).into(imageView)
        }
        camera.start(fragment.requireActivity(), fragment, requestCode)
    }
}