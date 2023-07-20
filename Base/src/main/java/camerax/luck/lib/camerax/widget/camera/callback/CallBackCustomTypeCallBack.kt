package camerax.luck.lib.camerax.widget.camera.callback

import android.net.Uri
import android.widget.RelativeLayout
import camerax.luck.lib.camerax.CustomCameraConfig
import camerax.luck.lib.camerax.listener.TypeListener
import camerax.luck.lib.camerax.utils.CameraUtils
import camerax.luck.lib.camerax.utils.FileUtils
import camerax.luck.lib.camerax.widget.camera.CustomCameraViewListener

class CallBackCustomTypeCallBack(private val viewListener: CustomCameraViewListener) : TypeListener {
    private val activity by lazy { viewListener?.getCurrentActivity() }
    private val context by lazy { viewListener?.getCurrentContext() }

    private val imageFormat by lazy { CustomCameraConfig.getConfig().imageFormat }

    override fun cancel() {
        viewListener?.onCancelMedia()
    }

    override fun confirm() {
        var outputPath = CustomCameraConfig.getOutputPath(activity?.intent)
        if (CameraUtils.isSaveExternal()) {
            outputPath = viewListener.isMergeExternalStorageState(outputPath)
        } else {
            // 对前置镜头导致的镜像进行一个纠正
            if (viewListener.isImageCaptureEnabled() && viewListener.isReversedHorizontal()) {
                val cameraFile = FileUtils.createCameraFile(context, CameraUtils.TYPE_IMAGE, imageFormat.type)
                if (FileUtils.copyPath(activity, outputPath, cameraFile.absolutePath)) {
                    outputPath = cameraFile.absolutePath
                    CustomCameraConfig.putOutputUri(activity?.intent, Uri.fromFile(cameraFile))
                }
            }
        }
        if (viewListener.isImageCaptureEnabled()) {
            viewListener.onCreateImagePreview()?.visibility = RelativeLayout.INVISIBLE
            viewListener.onCreateImagePreviewBg()?.alpha = 0f
            outputPath?.run {
                viewListener.onCreateCameraListener()?.onPictureSuccess(outputPath)
            }
        } else {
            viewListener.stopVideoPlay()
            outputPath?.run {
                viewListener.onCreateCameraListener()?.onRecordSuccess(outputPath)
            }
        }
    }
}