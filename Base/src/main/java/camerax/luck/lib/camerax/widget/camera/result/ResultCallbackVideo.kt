package camerax.luck.lib.camerax.widget.camera.result

import android.annotation.SuppressLint
import android.widget.RelativeLayout
import androidx.camera.core.VideoCapture
import androidx.camera.view.video.OnVideoSavedCallback
import camerax.luck.lib.camerax.CustomCameraConfig
import camerax.luck.lib.camerax.listener.CaptureListener
import camerax.luck.lib.camerax.utils.FileUtils
import camerax.luck.lib.camerax.widget.camera.CustomCameraViewListener
import camerax.luck.lib.camerax.widget.camera.callback.CallBackCustomSurfaceTexture

/**
 * 拍照回调
 */
@SuppressLint("RestrictedApi","UnsafeOptInUsageError")
class ResultCallbackVideo(private val listener: CustomCameraViewListener?, private var captureListener: CaptureListener):
    VideoCapture.OnVideoSavedCallback{

    private var recordTime: Long = 0
    private val surfaceTexture by lazy { CallBackCustomSurfaceTexture(listener) }

    override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
        val minSecond = CustomCameraConfig.getConfig().minDuration
        if (recordTime < minSecond || outputFileResults.savedUri == null) {
            return
        }
        val mTextureView = listener?.onCreateTextureView()
        val savedUri = outputFileResults.savedUri
        CustomCameraConfig.putOutputUri(listener?.getCurrentActivity()?.intent, savedUri)
        val outPutPath = if (FileUtils.isContent(savedUri.toString())) savedUri.toString() else savedUri!!.path!!
        mTextureView?.visibility = RelativeLayout.VISIBLE
        listener?.onCreateCurrentTimeTv()?.visibility = RelativeLayout.GONE
        if (mTextureView?.isAvailable == true) {
            listener?.startVideoPlay(outPutPath)
        } else {
            listener?.onCreateTextureView()?.surfaceTextureListener = surfaceTexture
        }
    }

    override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
        if (videoCaptureError == VideoCapture.ERROR_RECORDING_TOO_SHORT ||
            videoCaptureError == OnVideoSavedCallback.ERROR_MUXER) {
            captureListener.recordShort(0)
        } else {
            listener?.onCreateCameraListener()?.onError(videoCaptureError, message, cause)
        }
    }
}