package camerax.luck.lib.camerax.widget.camera.callback

import android.app.Activity
import android.graphics.SurfaceTexture
import android.view.TextureView
import camerax.luck.lib.camerax.CustomCameraConfig
import camerax.luck.lib.camerax.widget.camera.CustomCameraViewListener

class CallBackCustomSurfaceTexture(private val viewListener: CustomCameraViewListener?) : TextureView.SurfaceTextureListener {
    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        val mImagePreview = viewListener?.onCreateImagePreview()
        val context = mImagePreview?.context
        context?.run {
            val outputPath = CustomCameraConfig.getOutputPath((this as Activity).intent)
            viewListener?.startVideoPlay(outputPath)
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture,
        width: Int, height: Int) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = false

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
}