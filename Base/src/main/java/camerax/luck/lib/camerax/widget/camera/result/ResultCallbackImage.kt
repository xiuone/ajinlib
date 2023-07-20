package camerax.luck.lib.camerax.widget.camera.result

import android.app.Activity
import android.view.Surface
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import camerax.luck.lib.camerax.CustomCameraConfig
import camerax.luck.lib.camerax.utils.FileUtils
import camerax.luck.lib.camerax.widget.camera.CustomCameraViewListener

/**
 * 拍照回调
 */
class ResultCallbackImage(private val viewListener: CustomCameraViewListener?) : ImageCapture.OnImageSavedCallback {

    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
        val savedUri = outputFileResults.savedUri?:return
        val customCameraView = viewListener?.onCreateCustomCameraView()
        viewListener?.onCreateOrientationEventListener()?.stop()
        val mImagePreview = viewListener?.onCreateImagePreview()
        val captureLayout = viewListener?.onCreateCaptureLayout()
        val mImageCapture = viewListener?.onCreateImageCapture()

        if (mImagePreview != null) {
            val context = mImagePreview.context
            CustomCameraConfig.putOutputUri((context as Activity).intent, savedUri)
            mImagePreview.visibility = RelativeLayout.VISIBLE
            if (customCameraView != null ) {
                val targetRotation = mImageCapture?.targetRotation
                // 这种角度拍出来的图片宽比高大，所以使用ScaleType.FIT_CENTER缩放模式
                if (targetRotation == Surface.ROTATION_90 || targetRotation == Surface.ROTATION_270) {
                    mImagePreview.adjustViewBounds = true
                } else {
                    mImagePreview.adjustViewBounds = false
                    mImagePreview.scaleType = ImageView.ScaleType.FIT_CENTER
                }
                viewListener?.onCreateImagePreviewBg()?.animate()?.alpha(1f)?.setDuration(220)?.start()
            }
            val outPutCameraPath = if (FileUtils.isContent(savedUri.toString())) savedUri.toString() else savedUri.path
            viewListener?.onCreateImageCallbackListener()?.onLoadImage(outPutCameraPath, mImagePreview)
        }
        captureLayout?.setButtonCaptureEnabled(true)
        captureLayout?.startTypeBtnAnimator()
    }

    override fun onError(exception: ImageCaptureException) {
        viewListener?.onCreateCaptureLayout()?.setButtonCaptureEnabled(true)
        viewListener?.onCreateCameraListener()?.onError(exception.imageCaptureError, exception.message, exception.cause)
    }
}