package com.xy.picture.select.camera

import android.content.Intent
import android.provider.MediaStore
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.lib.camerax.SimpleCameraX
import com.luck.picture.lib.interfaces.OnCameraInterceptListener
import com.xy.base.R
import com.xy.base.utils.exp.getResString
import com.xy.base.utils.exp.getSdImageDir

class ImageCameraIntercept :OnCameraInterceptListener {


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