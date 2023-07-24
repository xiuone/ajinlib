package camerax.luck.lib.camerax.widget.camera.callback

import android.annotation.SuppressLint
import android.view.View
import android.widget.RelativeLayout
import androidx.camera.core.ImageCapture
import androidx.camera.core.VideoCapture
import androidx.camera.view.LifecycleCameraController
import camerax.luck.lib.camerax.CustomCameraConfig
import camerax.luck.lib.camerax.listener.CaptureListener
import camerax.luck.lib.camerax.utils.CameraUtils
import camerax.luck.lib.camerax.utils.FileUtils
import camerax.luck.lib.camerax.widget.camera.CustomCameraViewListener
import camerax.luck.lib.camerax.widget.camera.result.ResultCallbackImage
import camerax.luck.lib.camerax.widget.camera.result.ResultCallbackVideo
import xy.xy.base.R
import java.io.File

@SuppressLint("MissingPermission", "RestrictedApi","UnsafeOptInUsageError")
class CallBackCustomCapture(private val listener: CustomCameraViewListener?) : CaptureListener{
    private val imageResultCallback by lazy { ResultCallbackImage(listener) }
    private val videoResultCallback by lazy { ResultCallbackVideo(listener,this) }
    private val context by lazy { listener?.getCurrentContext() }
    private var recordTime: Long = 0
    //是否显示录制时间
    private val isDisplayRecordTime by lazy { CustomCameraConfig.getConfig().isDisplayRecordTime }

    //相机模式
    private var useCameraCases = LifecycleCameraController.IMAGE_CAPTURE

    //图片文件类型
    private val imageFormat by lazy { CustomCameraConfig.getConfig().imageFormat }

    //视频文件类型
    private val videoFormat by lazy { CustomCameraConfig.getConfig().videoFormat }

    fun isImageCaptureEnabled() =  useCameraCases == LifecycleCameraController.IMAGE_CAPTURE


    override fun takePictures() {
        val mImageCapture = listener?.onCreateImageCapture()?:return
        if (listener.onCreateProcessCameraProvider()?.isBound(mImageCapture) == false) {
            listener.bindCameraImageUseCases()
        }
        useCameraCases = LifecycleCameraController.IMAGE_CAPTURE
        listener.onCreateCaptureLayout()?.setButtonCaptureEnabled(false)
        listener.onCreateSwitchCamera()?.visibility = RelativeLayout.INVISIBLE
        listener.onCreateFlashImageView()?.visibility = RelativeLayout.INVISIBLE
        listener.onCreateCurrentTimeTv()?.visibility = RelativeLayout.GONE
        val metadata = ImageCapture.Metadata()
        metadata.isReversedHorizontal = listener.isReversedHorizontal()
        val cameraFile: File = if (CameraUtils.isSaveExternal()) {
            FileUtils.createTempFile(context, false)
        } else {
            FileUtils.createCameraFile(context, CameraUtils.TYPE_IMAGE, imageFormat.type)
        }
        val fileOptions = ImageCapture.OutputFileOptions.Builder(cameraFile).setMetadata(metadata).build()
        listener.onCreateExecutor()?.run {
            mImageCapture.takePicture(fileOptions,this,imageResultCallback)
        }
    }

    override fun recordStart() {
        val mVideoCapture = listener?.onCreateVideoCapture()?:return
        if (listener.onCreateProcessCameraProvider()?.isBound(mVideoCapture) == false) {
            listener.bindCameraVideoUseCases()
        }
        useCameraCases = LifecycleCameraController.VIDEO_CAPTURE
        listener.onCreateSwitchCamera()?.visibility = RelativeLayout.INVISIBLE
        listener.onCreateFlashImageView()?.visibility = RelativeLayout.INVISIBLE
        listener.onCreateCurrentTimeTv()?.visibility = if (isDisplayRecordTime) RelativeLayout.VISIBLE else RelativeLayout.GONE
        val fileOptions: VideoCapture.OutputFileOptions
        val cameraFile: File = if (CameraUtils.isSaveExternal()) {
            FileUtils.createTempFile(context, true)
        } else {
            FileUtils.createCameraFile(context, CameraUtils.TYPE_VIDEO, videoFormat.type)
        }
        fileOptions = VideoCapture.OutputFileOptions.Builder(cameraFile).build()

        listener.onCreateExecutor()?.run {
            listener.onCreateVideoCapture()?.startRecording(fileOptions,this,videoResultCallback)
        }
    }

    override fun changeTime(duration: Long) {
        val currentTimeTv = listener?.onCreateCurrentTimeTv()
        if (isDisplayRecordTime &&  currentTimeTv?.visibility == RelativeLayout.VISIBLE) {
            val min = duration/1000/60
            val second = duration/1000%60
            val secondStr = (if (second < 10) "0" else "" )+ second
            currentTimeTv.text = "$min:$secondStr"
            if (duration < 1000){
                currentTimeTv.visibility = View.GONE
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun recordShort(time: Long) {
        recordTime = time
        listener?.onCreateSwitchCamera()?.visibility = RelativeLayout.VISIBLE
        listener?.onCreateFlashImageView()?.visibility = RelativeLayout.VISIBLE
        listener?.onCreateCurrentTimeTv()?.visibility = RelativeLayout.GONE
        listener?.onCreateCaptureLayout()?.resetCaptureLayout()
        listener?.onCreateCaptureLayout()?.setTextWithAnimation(context?.getString(R.string.picture_recording_time_is_short))
        try {
            listener?.onCreateVideoCapture()?.stopRecording()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("RestrictedApi")
    override fun recordEnd(time: Long) {
        recordTime = time
        try {
            listener?.onCreateVideoCapture()?.stopRecording()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun recordError() {
        listener?.onCreateCameraListener()?.onError(0, "An unknown error", null)
    }
}