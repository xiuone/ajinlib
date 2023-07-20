package camerax.luck.lib.camerax.utils

import android.app.Activity
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import camerax.luck.lib.camerax.CustomCameraConfig
import camerax.luck.lib.camerax.type.CameraImageFormatQ
import camerax.luck.lib.camerax.type.CameraVideoFormatQ
import java.io.FileInputStream
import java.io.FileNotFoundException

/**
 * @author：luck
 * @date：2021/11/8 4:27 下午
 * @describe：CameraFileUtils
 */
object CameraUtils {
    const val TYPE_IMAGE = 1
    const val TYPE_VIDEO = 2


    fun isSaveExternal() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    /**
     * 构建图片的ContentValues,用于保存拍照后的照片
     *
     * @param cameraFileName 资源名称
     * @param mimeType       资源类型
     * @return
     */
    fun buildImageContentValues( mimeType: CameraImageFormatQ): ContentValues {
        val time = System.currentTimeMillis().toString()
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        val values = ContentValues(3)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, DateUtils.getCreateFileName("IMG_"))
        values.put(MediaStore.Images.Media.MIME_TYPE,mimeType.type)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, time)
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Camera")
        }
        return values
    }

    /**
     * 构建视频的ContentValues,用于保存拍照后的照片
     *
     * @param cameraFileName 资源名称
     * @param mimeType       资源类型
     * @return
     */
    fun buildVideoContentValues(mimeType: CameraVideoFormatQ): ContentValues {
        val time = System.currentTimeMillis().toString()
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        val values = ContentValues(3)
        values.put(MediaStore.Video.Media.DISPLAY_NAME, DateUtils.getCreateFileName("VID_"))
        values.put(MediaStore.Video.Media.MIME_TYPE, mimeType.type)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Video.Media.DATE_TAKEN, time)
            values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
        }
        return values
    }


    /**
     * 水平镜像
     *
     * @param bmp
     * @return
     */
    fun toHorizontalMirror(bmp: Bitmap): Bitmap {
        val w = bmp.width
        val h = bmp.height
        val matrix = Matrix()
        matrix.postScale(-1f, 1f)
        matrix.postRotate((if (w > h) 90 else 0).toFloat())
        return Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true)
    }

    fun computeSize(srcWidth: Int, srcHeight: Int): Int {
        var srcWidth = srcWidth
        var srcHeight = srcHeight
        srcWidth = if (srcWidth % 2 == 1) srcWidth + 1 else srcWidth
        srcHeight = if (srcHeight % 2 == 1) srcHeight + 1 else srcHeight
        val longSide = Math.max(srcWidth, srcHeight)
        val shortSide = Math.min(srcWidth, srcHeight)
        val scale = shortSide.toFloat() / longSide
        return if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                1
            } else if (longSide < 4990) {
                2
            } else if (longSide > 4990 && longSide < 10240) {
                4
            } else {
                longSide / 1280
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            if (longSide / 1280 == 0) 1 else longSide / 1280
        } else {
            Math.ceil(longSide / (1280.0 / scale)).toInt()
        }
    }
}