package camerax.luck.lib.camerax.widget.camera

import android.app.Activity
import android.content.Context
import android.view.TextureView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.VideoCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import camerax.luck.lib.camerax.listener.CameraListener
import camerax.luck.lib.camerax.listener.ImageCallbackListener
import camerax.luck.lib.camerax.widget.capture.CaptureLayout
import camerax.luck.lib.camerax.widget.flash.FlashImageView
import camerax.luck.lib.camerax.widget.focus.FocusImageView
import java.util.concurrent.Executor

interface CustomCameraViewListener {
    fun getCurrentContext():Context
    fun getCurrentActivity():Activity?

    fun onCreateCustomCameraView():CustomCameraView?
    fun onCreateImagePreview(): ImageView?
    fun onCreateImagePreviewBg(): View?
    fun onCreateCaptureLayout(): CaptureLayout?
    fun onCreateFlashImageView(): FlashImageView?
    fun onCreateCurrentTimeTv(): TextView?
    fun onCreatePreviewView(): PreviewView?
    fun onCreateFocusImageView(): FocusImageView?
    fun onCreateSwitchCamera(): ImageView?
    fun onCreateTextureView(): TextureView?

    fun onCreateCameraListener(): CameraListener?
    fun onCreateImageCallbackListener(): ImageCallbackListener?
    fun onCreateOrientationEventListener(): OrientationEventListener?
    fun onCreateCameraInfo(): CameraInfo?
    fun onCreateImageCapture(): ImageCapture?
    fun onCreateCameraControl(): CameraControl?
    fun onCreateImageAnalysis(): ImageAnalysis?
    fun onCreateVideoCapture(): VideoCapture?
    fun onCreateExecutor(): Executor?
    fun onCreateProcessCameraProvider(): ProcessCameraProvider?
    fun onCreateDisplayId(): Int?
    fun bindCameraImageUseCases()
    fun bindCameraVideoUseCases()
    fun isReversedHorizontal():Boolean
    fun onCancelMedia()
    fun isImageCaptureEnabled():Boolean
    fun isMergeExternalStorageState(outputPath:String?):String?

    fun startVideoPlay(path:String?)
    fun stopVideoPlay()


}