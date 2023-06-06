package com.jianbian.face.utils

import android.graphics.Point
import android.hardware.Camera
import com.xy.base.utils.Logger
import java.util.*

object CameraUtils {
    private var TAG = "GameraUtils"

    private const val MIN_PREVIEW_PIXELS = 640 * 480
    private const val MAX_PREVIEW_PIXELS = 1920 * 1080



    fun releaseCamera(camera: Camera?) {
        try {
            camera?.release()
        } catch (e2: Exception) {
            e2.printStackTrace()
            Logger.e("$TAG== releaseCamera:${e2.toString()}")
        }
    }


    fun getBestPreview(parameters: Camera.Parameters?, screenResolution: Point): Point {
        val rawSupportedSizes = parameters?.supportedPreviewSizes ?: return Point(640, 480)
        // return new Point(defaultSize.width, defaultSize.height);
        val supportedPictureSizes: MutableList<Camera.Size> = ArrayList(rawSupportedSizes)
        Collections.sort(supportedPictureSizes,
            Comparator { a, b ->
                val aPixels = a.height * a.width
                val bPixels = b.height * b.width
                if (bPixels < aPixels) {
                    return@Comparator -1
                }
                if (bPixels > aPixels) {
                    1
                } else 0
            })
        val screenAspectRatio =
            if (screenResolution.x > screenResolution.y) screenResolution.x.toDouble() / screenResolution.y.toDouble() else screenResolution.y.toDouble() / screenResolution.x.toDouble()
        var selectedSize: Camera.Size? = null
        var selectedMinus = -1.0
        var selectedPreviewSize = 0.0
        val it = supportedPictureSizes.iterator()
        while (it.hasNext()) {
            val supportedPreviewSize = it.next()
            val realWidth = supportedPreviewSize.width
            val realHeight = supportedPreviewSize.height
            //            Log.e(TAG, "preview size " + realWidth + " " + realHeight);
            if (realWidth * realHeight < MIN_PREVIEW_PIXELS) {
                it.remove()
                continue
            } else if (realWidth * realHeight > MAX_PREVIEW_PIXELS) {
                it.remove()
                continue
            } else if (realHeight % 4 != 0 || realWidth % 4 != 0) {
                it.remove()
                continue
            } else {
                val aRatio =
                    if (supportedPreviewSize.width > supportedPreviewSize.height) supportedPreviewSize.width.toDouble() / supportedPreviewSize.height.toDouble() else supportedPreviewSize.height.toDouble() / supportedPreviewSize.width.toDouble()
                val minus = Math.abs(aRatio - screenAspectRatio)
                var selectedFlag = false
                if (selectedMinus == -1.0 && minus <= 0.25f
                    || selectedMinus >= minus && minus <= 0.25f
                ) {
                    selectedFlag = true
                }
                if (selectedFlag) {
                    selectedMinus = minus
                    selectedSize = supportedPreviewSize
                    selectedPreviewSize = (realWidth * realHeight).toDouble()
                }
            }
        }
        return if (selectedSize != null) {
            val preview: Camera.Size = selectedSize
            Point(preview.width, preview.height)
        } else {
            val defaultSize = parameters.previewSize
            // return new Point(defaultSize.width, defaultSize.height);
            Point(640, 480)
        }
    }
}