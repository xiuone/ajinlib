package xy.xy.base.picture.select.camera

import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.luck.lib.camerax.SimpleCameraX
import com.luck.picture.lib.interfaces.OnCameraInterceptListener
import xy.xy.base.picture.PictureSelectorUtils


class ImageCameraIntercept : OnCameraInterceptListener {


    override fun openCamera(fragment: Fragment?, cameraMode: Int, requestCode: Int) {
        fragment?.run {
            val camera = SimpleCameraX.of()
            camera.isAutoRotation(true)
            camera.setCameraMode(cameraMode)
            camera.setVideoFrameRate(25)
            camera.setVideoBitRate(3 * 1024 * 1024)
            camera.isDisplayRecordChangeTime(true)
            camera.isManualFocusCameraPreview(true)
            camera.isZoomCameraPreview(true)
            camera.setOutputPathDir(PictureSelectorUtils.getOutPath(PictureSelectorUtils.SelectType.IMAGE))
            camera.setImageEngine { context, url, imageView ->
                Glide.with(context).load(url).into(imageView)
            }
            camera.start(requireActivity(), this, requestCode)
        }
    }
}